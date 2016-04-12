package rpg.client;

import rpg.Application;
import rpg.screen.ClientScreen;
import rpg.world.AsciiSymbol;

import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {
    private ConnectionToServer server;
    private LinkedBlockingQueue<Object> messages;
    private LinkedBlockingQueue<int[]> keycodes;
    private Socket socket;
    private Application app;


    public static void main(String[] args) throws IOException {
        Client client = new Client(InetAddress.getLocalHost().getHostAddress(),1336);

    }

    public Client(String IPAddress, int port) throws IOException{
        app = new Application(0);
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setVisible(true);
        socket = new Socket(IPAddress, port);
        messages = new LinkedBlockingQueue<Object>();
        server = new ConnectionToServer(socket);
        keycodes = new LinkedBlockingQueue<>();


        Thread messageHandling = new Thread() {
            public void run(){
                while(true){
                    try{
                        Object message = messages.take();
                        // Do some handling here...
                        if(message instanceof AsciiSymbol[][]){
                            app.getScreen().setView((AsciiSymbol[][]) message);
                            System.out.println("got view");
                            app.getScreen().displayOutput(app.getTerminal());
                            //app.getScreen().displayOutput(new AsciiPanel(80,24));
                            app.repaint();
                        }
                        System.out.println(message.getClass());
                        System.out.println("Message Received: " + message);
                    }
                    catch(InterruptedException e){ }
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
                            messages.put(obj);

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
                                //System.out.println("class type clientscreen");
                                ClientScreen clientScreen = (ClientScreen) app.getScreen();
                                int[] gotKeycodes = clientScreen.getKeycodes();
                                if(gotKeycodes.length>0) {
                                    keycodes.add(gotKeycodes);
                                }
                                //System.out.println("clientscreen.getkeycodes in Client"+clientScreen.getKeycodes());

                            }
                            if(keycodes.size()>0) {
                                System.out.println("write thread to send");
                                int[] sendingCodes = keycodes.take();
                                if(sendingCodes.length>0) {
                                    send(sendingCodes);
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