package rpg.server;

import org.apache.commons.io.IOUtils;
import rpg.Application;
import rpg.client.ClientData;
import rpg.client.PlayerData;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {

    private LinkedBlockingQueue<ClientData> clientMessages = new LinkedBlockingQueue<>();
    ServerSocket serverSocket;
    private Application app;
    private Map<Integer, Connection> clientMap = Collections.synchronizedMap(new HashMap<>());
    Thread accept;
    Thread messageHandling;

    public Server(int port, Application app) {
        this.app = app;
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        accept = new Thread() {
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
                        //app.newConnection(randomIndex);
                        sendToOne(randomIndex,randomIndex);
                    } catch (SocketException e){
                        System.out.println("Socket failed");
                        try {
                            shutDown();
                        } catch (IOException e1){
                            throw new RuntimeException(e1);
                        }
                        break;
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        accept.setDaemon(true);
        accept.start();

        messageHandling = new Thread(){
            public void run(){
                while(true){
                    try {
                        ClientData clientDataReceived = clientMessages.take();
                        if (clientDataReceived != null) {
                            app.executeKeyCode(clientDataReceived);
                        }

                    } catch (InterruptedException ie) {
                        break;
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
        Thread read;

        public Connection(Socket socket) throws IOException{
            this.socket = socket;
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());

            read = new Thread(){
                public void run(){
                    while(true){
                        try{
                            Object obj = in.readObject();
                            if (obj instanceof PlayerData){
                                PlayerData playerData = (PlayerData) obj;
                                app.newConnection(playerData);
                            }
                            if (obj instanceof ClientData) {
                                ClientData clientdata = (ClientData) obj;
                                clientMessages.put(clientdata);
                            }
                        } catch (EOFException e) {
                            System.out.println("Client disappeared");
                            break;
                        } catch (SocketException s){
                            break;
                        } catch (ClassNotFoundException e){
                            throw new RuntimeException(e);
                        } catch (InterruptedException e){
                            break;
                        } catch (IOException e){
                            throw new RuntimeException(e);
                        }
                    }
                }
            };

            read.setDaemon(true);
            read.start();

        }

        public void write(Object obj) throws IOException{
            out.reset();
            out.writeObject(obj);
        }
        public void close() throws IOException{
            closeLoudly(out,in,socket);
            read.interrupt();
        }
    }

    public synchronized void sendToOne(int index, Object message) {
        try {
            clientMap.get(index).write(message);
        } catch (SocketException e) {
            System.out.println("Client disconnected");
            app.onDisconnect(index);
            clientMap.remove(index);
        } catch (IOException ioe){
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

    public void shutDown() throws IOException {
        accept.interrupt();
        messageHandling.interrupt();
        for (Connection connection : clientMap.values()) {
            connection.close();
        }
        closeLoudly(serverSocket);
    }

    public void kick(int id) {
        try {
            clientMap.get(id).close();
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("exception while kicking client");
        }
    }

    public void closeLoudly(Closeable... closeables) throws IOException {

        IOException exceptionToThrow = null;

        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                if (exceptionToThrow == null) {
                    exceptionToThrow = e;
                }
            }
        }

        if (exceptionToThrow != null) {
            throw exceptionToThrow;
        }
    }

}
