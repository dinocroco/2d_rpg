package rpg.server;

import rpg.Application;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    private List<Connection> connections = new ArrayList<>();
    private LinkedBlockingQueue messages = new LinkedBlockingQueue();
    ServerSocket serverSocket;
    private Application app;

    public Server(int port, Application app) {
        this.app = app;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        Thread accept = new Thread() {
            public void run() {
                while (true) {
                    try {
                        Socket s = serverSocket.accept();
                        connections.add(new Connection(s));
                        app.newConnection();
                        //connections.get(connections.size()-1).write(app.getScreen());
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        };

        accept.setDaemon(true);
        accept.start();

        Thread messageHandling = new Thread(){
            public void run(){
                while(true){
                    try{
                        Object message = messages.take();
                        //handling

                    } catch (InterruptedException ie){
                        ie.printStackTrace();
                    }
                }
            }
        };
        messageHandling.setDaemon(true);
        messageHandling.start();
    }
    private class Connection {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket socket;

        public Connection(Socket socket) throws IOException{
            this.socket = socket;
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            Thread read = new Thread(){
                public void run(){
                    while(true){
                        try{
                            Object obj = in.readObject();
                            messages.put(obj);
                        } catch (Exception ioe){
                            ioe.printStackTrace();
                        }
                    }
                }
            };

            read.setDaemon(true);
            read.start();

        }

        public void write(Object obj){
            try {
                out.writeObject(obj);
            } catch (IOException ioe){
                ioe.printStackTrace();
            }
        }

    }

    public void sendToOne(int index, Object message) throws IndexOutOfBoundsException {
        connections.get(index).write(message);
    }

    public void sendToAll(Object message){
        for (Connection connection:connections){
            connection.write(message);
        }
    }

}
