package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by mrrobot on 24.03.16.
 */
public class Client {

    public static void main(String[] args) throws Exception{

        try(Socket socket = new Socket(InetAddress.getLocalHost(),1336);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream())){

            Scanner sc = new Scanner(System.in);
            while(true){
                String s = dis.readUTF();
                System.out.println(s);
            }

        }

    }
}
