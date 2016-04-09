package rpg.client;

import asciiPanel.AsciiPanel;
import rpg.Application;
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
    private Socket socket;
    private Application app;

    public static void main(String[] args) throws IOException {
        Client client = new Client(InetAddress.getLocalHost().getHostAddress(),1336);

        client.app = new Application(0);
        client.app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.app.setVisible(true);
    }

    public Client(String IPAddress, int port) throws IOException{
        socket = new Socket(IPAddress, port);
        messages = new LinkedBlockingQueue<Object>();
        server = new ConnectionToServer(socket);

        Thread messageHandling = new Thread() {
            public void run(){
                while(true){
                    try{
                        Object message = messages.take();
                        // Do some handling here...
                        if(message instanceof AsciiSymbol[][]){
                            app.getScreen().setView((AsciiSymbol[][]) message);
                            //app.getScreen().displayOutput(new AsciiPanel(80,24));
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

            //read.setDaemon(true);
            read.start();
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
