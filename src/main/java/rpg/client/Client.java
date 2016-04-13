package rpg.client;

import rpg.Application;
import rpg.screen.ClientScreen;
import rpg.world.AsciiSymbol;
import rpg.world.Diff;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
    private ConnectionToServer server;
    private LinkedBlockingQueue<AsciiSymbol[][]> asciiMessages;
    private LinkedBlockingQueue<Diff> diffs;
    private LinkedBlockingQueue<int[]> keycodes;
    private Socket socket;
    private Application app;
    private int idCode;

    public static void main(String[] args) throws IOException {
        Client client = new Client(InetAddress.getLocalHost().getHostAddress(),1336);

    }

    public Client(String IPAddress, int port) throws IOException{
        app = new Application(0);
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setVisible(true);
        socket = new Socket(IPAddress, port);
        asciiMessages = new LinkedBlockingQueue<>();
        server = new ConnectionToServer(socket);
        keycodes = new LinkedBlockingQueue<>();
        diffs = new LinkedBlockingQueue<>();


        Thread messageHandling = new Thread() {
            public void run(){
                while(true){
                    try{
                        AsciiSymbol[][] asciiView = asciiMessages.poll();
                        Diff diff = diffs.poll();
                        if (asciiView != null){
                            app.getScreen().setView(asciiView);
                            app.getScreen().displayOutput(app.getTerminal());
                            //app.getScreen().displayOutput(new AsciiPanel(80,24));
                            app.repaint();
                        }
                        sleep(50);

                        //System.out.println(asciiView.getClass());
                        //System.out.println("Message Received: " + asciiView);
                    } catch (InterruptedException e){
                        throw new RuntimeException(e);
                    }

                }
            }
        };

        //messageHandling.setDaemon(true);
        messageHandling.start();
    }

    private class ConnectionToServer {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket socket;

        ConnectionToServer(Socket socket) throws IOException {
            this.socket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            Thread read = new Thread(){
                public void run(){
                    while(true){
                        try{
                            Object obj = in.readObject();
                            if (obj instanceof Integer){
                                int index = (int) obj;
                                System.out.println("received index: "+index);
                                idCode = index;
                            } else if (obj instanceof AsciiSymbol[][]) {
                                AsciiSymbol[][] asciiArray = (AsciiSymbol[][]) obj;
                                asciiMessages.put(asciiArray);
                            } else if (obj instanceof Diff){
                                Diff diff = (Diff) obj;
                                diffs.put(diff);
                            }

                        }
                        catch(Exception e){
                            //e.printStackTrace();
                        }
                    }
                }
            };

            Thread write = new Thread(){
                public void run(){
                    while(true){
                        try{
                            if (app.getScreen().getClass()== ClientScreen.class ){
                                ClientScreen clientScreen = (ClientScreen) app.getScreen();
                                int[] gotKeycodes = clientScreen.getKeycodes();
                                if(gotKeycodes.length>0) {
                                    keycodes.add(gotKeycodes);
                                }
                                //System.out.println("clientscreen.getkeycodes in Client"+clientScreen.getKeycodes());

                            }
                            if(keycodes.size()>0) {
                                int[] sendingCodes = keycodes.take();
                                ClientData dataToSend = new ClientData(idCode);
                                dataToSend.addKeycodes(sendingCodes);

                                if(sendingCodes.length>0) {
                                    send(dataToSend);

                                }
                            } else {
                                Thread.sleep(50);
                            }
                        } catch (InterruptedException e){
                            throw new RuntimeException(e);
                        }
                    }
                }
            };

            //read.setDaemon(true);
            read.start();
            write.start();
        }

        private void write(Object obj) {
            try{
                out.writeObject(obj);
            }
            catch(IOException e){
                //e.printStackTrace();
            }
        }


    }

    public void send(Object obj) {
        server.write(obj);
    }
}
