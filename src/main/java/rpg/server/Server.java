package rpg.server;

import org.apache.commons.io.IOUtils;
import rpg.Application;
import rpg.client.ClientData;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    //private List<Connection> connections = new ArrayList<>();
    private LinkedBlockingQueue<ClientData> clientMessages = new LinkedBlockingQueue<>();
    ServerSocket serverSocket;
    private Application app;
    private Map<Integer, Connection> clientMap = Collections.synchronizedMap(new HashMap<>());

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
                        sendToOne(randomIndex,new Integer(randomIndex));
                        //connections.get(connections.size()-1).write(app.getScreen());
                    } catch (SocketException e){
                        System.out.println("socket failed");
                        e.printStackTrace();
                        break;
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
                    try {
                        ClientData clientDataReceived = clientMessages.take();
                        if (clientDataReceived != null) {
                            app.executeKeyCode(clientDataReceived);
                        }

                    } catch (InterruptedException ie) {
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
                            if (obj instanceof ClientData) {
                                ClientData clientdata = (ClientData) obj;
                                clientMessages.put(clientdata);
                            }
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

        public void write(Object obj) throws IOException{
            out.reset();
            //System.out.println("sending"+obj.getClass());
            out.writeObject(obj);
        }
        public void close() throws IOException {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(socket);
        }
    }

    public synchronized void sendToOne(int index, Object message) {
        try {
            clientMap.get(index).write(message);
        } catch (SocketException e) {
            System.out.println("Client disconnected");
            app.onDisconnect(index);
            clientMap.remove(index);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public synchronized void sendToAll(Object message){
        List<Integer> ids = new ArrayList<>();
        for (int id : clientMap.keySet()){
            try {
                clientMap.get(id).write(message);
            } catch (SocketException e){
                System.out.println("Client disconnected");
                app.onDisconnect(id);
                ids.add(id);
            } catch (IOException ioe){
                ioe.printStackTrace();
            }
        }
        for (Integer id : ids) {
            clientMap.remove(id);
        }
    }

    public void shutDown() throws IOException{
        for (Connection connection : clientMap.values()) {
            connection.close();
        }
        serverSocket.close();
    }

}
