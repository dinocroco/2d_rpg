package rpg.client;

import org.apache.commons.io.IOUtils;
import rpg.Application;
import rpg.character.Player;
import rpg.screen.ClientScreen;
import rpg.world.AsciiSymbol;
import rpg.world.Diff;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;


public class Client {

    private static final String FILENAME = "clientData.txt";

    private ConnectionToServer server;
    private LinkedBlockingQueue<AsciiSymbol[][]> asciiMessages;
    private LinkedBlockingQueue<Diff> diffs;
    private LinkedBlockingQueue<KeyEventWrapper[]> keyevents;
    private Socket socket;
    private Application app;
    private int idCode;
    private String playername = null;
    private String serverIp = null;
    private String password = null;
    private boolean serverOpen;
    private Client client;


    public static void main(String[] args) throws IOException{

        Client client = new Client(Application.PORT);

    }

    public Client(int port) throws IOException{
        this.client = this;
        getClientPreferences();

        if(serverIp == null){
            serverIp = InetAddress.getLocalHost().getHostAddress();
        }
        try {
            socket = new Socket(serverIp, port);
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
        keyevents = new LinkedBlockingQueue<>();
        diffs = new LinkedBlockingQueue<>();


        Thread messageHandling = new Thread() {
            public void run(){
                try{
                    while(idCode==0){
                        //System.out.println("Waiting for idCode");
                        Thread.sleep(50);
                    }
                    if (playername == null){
                        playername = idCode+"";
                    }
                    while(serverOpen){
                        AsciiSymbol[][] asciiView = asciiMessages.poll();
                        Diff diff = diffs.poll();
                        if (asciiView != null){
                            app.getScreen().setView(asciiView);
                            app.getScreen().setPlayerId(idCode);
                            app.getScreen().displayOutput(app.getTerminal());
                            app.repaint();
                        }
                        if(diff != null){
                            app.getScreen().parseDiff(diff);
                            app.getScreen().displayOutput(app.getTerminal());
                            app.repaint();
                        }
                        sleep(50);


                    }
                } catch (InterruptedException e){
                    throw new RuntimeException(e);
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

    private class ConnectionToServer    {
        ObjectInputStream in;
        ObjectOutputStream out;
        Socket socket;
        boolean playerDataSent = false;

        ConnectionToServer(Socket socket) throws IOException {
            this.socket = socket;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e){
                closeConnection();
                throw e;
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

                            if(!playerDataSent){
                                PlayerData playerdata = new PlayerData(idCode,playername,password);
                                send(playerdata);
                                playerDataSent = true;
                            }
                            if (app.getScreen().getClass()== ClientScreen.class ){
                                KeyEventWrapper[] gotKeyEvents = app.getScreen().getKeyEvents();
                                if(gotKeyEvents.length>0) {
                                    keyevents.add(gotKeyEvents);
                                }

                            }
                            if(keyevents.size()>0) {
                                KeyEventWrapper[] sendingEvents = keyevents.take();
                                ClientData dataToSend = new ClientData(idCode);
                                dataToSend.addKeyEvents(sendingEvents);

                                if(sendingEvents.length>0) {
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

    public void getClientPreferences() throws IOException{
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FILENAME)))) {

            String name = in.readLine();
            if (name != null) {
                playername = name;
            }

            String passWord = in.readLine();
            if(passWord != null){
                password = passWord;
            }

            String ipAddress = in.readLine();
            if (ipAddress != null){
                serverIp = ipAddress;
            }

        }
    }

}
