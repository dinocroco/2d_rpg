package rpg.server;

import rpg.Application;
import rpg.client.ClientData;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    //private List<Connection> connections = new ArrayList<>();
    private LinkedBlockingQueue messages = new LinkedBlockingQueue();
    ServerSocket serverSocket;
    private Application app;
    private Map<Integer, Connection> clientMap = Collections.synchronizedMap(new HashMap<>());
    private Map<Integer, Connection> clientMap = Collections.synchronizedMap(new HashMap<Integer, Connection>());
    private LinkedBlockingQueue<Integer> idCodes = new LinkedBlockingQueue<>();


    public Server(int port, Application app) {
        this.app = app;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        Thread accept = new Thread() {
            public void run() {
                Random random = new Random();
                while (true) {
                    try {
                        Socket s = serverSocket.accept();
                        int randomIndex;
                        do {
                            randomIndex = random.nextInt(998)+1;
                        } while (clientMap.containsKey(randomIndex));
                        clientMap.put(randomIndex, new Connection(s));
                        app.newConnection(randomIndex);
                        idCodes.put(randomIndex);
                        app.newConnection();
                        //connections.get(connections.size()-1).write(app.getScreen());
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    } catch (InterruptedException i){
                        throw new RuntimeException(i);
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
                        if (message instanceof ClientData){
                            System.out.println("received keycodes in server");
                            ClientData clientData = (ClientData) message;
                            app.executeKeyCode(clientData.getKeycodes());
                        }
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
                        } catch (EOFException e) {
                            System.out.println("Client disappeared");
                            break;
                        } catch (SocketException s){
                            break;
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

    public void sendToOne(int index, Object message) {
        clientMap.get(index).write(message);
    }

    public void sendToAll(Object message){
        for (int id:clientMap.keySet()){
            clientMap.get(id).write(message);
        }
    }

}
