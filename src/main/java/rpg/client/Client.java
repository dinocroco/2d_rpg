package rpg.client;

import org.apache.commons.io.IOUtils;
import rpg.Application;
import rpg.screen.ClientScreen;
import rpg.world.AsciiSymbol;
import rpg.world.Diff;

import javax.swing.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;


public class Client {
    private ConnectionToServer server;
    private LinkedBlockingQueue<AsciiSymbol[][]> asciiMessages;
    private LinkedBlockingQueue<Diff> diffs;
    private LinkedBlockingQueue<int[]> keycodes;
    private Socket socket;
    private Application app;
    private int idCode;
    private boolean serverOpen;
    private Client client;

    public static void main(String[] args) throws IOException{

        Client client = new Client(InetAddress.getLocalHost().getHostAddress(),1336);

    }

    public Client(String IPAddress, int port) throws IOException{
        this.client = this;
        try {
            socket = new Socket(IPAddress, port);
            serverOpen = true;
        } catch (ConnectException e){
            System.out.println("There is no server running");
            return;
        }
        app = new Application(0);
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        app.setVisible(true);
        asciiMessages = new LinkedBlockingQueue<>();
        server = new ConnectionToServer(socket);
        keycodes = new LinkedBlockingQueue<>();
        diffs = new LinkedBlockingQueue<>();


        Thread messageHandling = new Thread() {
            public void run(){
                while(serverOpen){
                    try{
                        AsciiSymbol[][] asciiView = asciiMessages.poll();
                        Diff diff = diffs.poll();
                        if (asciiView != null){
                            app.getScreen().setView(asciiView);
                            ((ClientScreen)app.getScreen()).setPlayerId(idCode);
                            app.getScreen().displayOutput(app.getTerminal());
                            app.repaint();
                        }
                        if(diff != null){
                            ((ClientScreen)app.getScreen()).parseDiff(diff);
                            app.getScreen().displayOutput(app.getTerminal());
                            app.repaint();
                        }
                        sleep(50);
                    } catch (InterruptedException e){
                        throw new RuntimeException(e);
                    }

                }
                app.setDisconnectScreen();
                app.repaint();
            }
        };

        messageHandling.start();
    }

    public void closeConnection(){
        this.server.close();
        IOUtils.closeQuietly(this.socket);
    }

    private class ConnectionToServer {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket socket;

        ConnectionToServer(Socket socket) {
            this.socket = socket;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e){
                closeConnection();
                return;
            }


            Thread read = new Thread(){
                public void run(){
                    while(serverOpen){
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

                        } catch (InterruptedException e) {
                            serverOpen = false;
                        }catch (EOFException e){
                            break;
                        } catch (ClassNotFoundException e){
                            throw new RuntimeException(e);
                        } catch (IOException e){
                            client.closeConnection();
                            serverOpen = false;
                        }
                    }
                }
            };

            Thread write = new Thread(){
                public void run(){
                    while(serverOpen){
                        try{
                            if (app.getScreen().getClass()== ClientScreen.class ){
                                ClientScreen clientScreen = (ClientScreen) app.getScreen();
                                int[] gotKeycodes = clientScreen.getKeycodes();
                                if(gotKeycodes.length>0) {
                                    keycodes.add(gotKeycodes);
                                }

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
                            serverOpen = false;
                        } catch (SocketException e){
                            System.out.println("server disconnected");
                            serverOpen = false;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    IOUtils.closeQuietly(in);
                    IOUtils.closeQuietly(out);
                    IOUtils.closeQuietly(socket);

                }
            };

            read.start();
            write.start();
        }

        private void write(Object obj) throws IOException {
            out.reset();
            out.writeObject(obj);

        }

        public void close(){
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(socket);
        }

    }

    public void send(Object obj) throws IOException {
        server.write(obj);
    }
}
