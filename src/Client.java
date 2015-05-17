

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Master on 11-May-15.
 */
public class Client {
    public static void main(String[] args) throws IOException {

        final Socket s = new Socket();
        s.connect(new InetSocketAddress("localhost",10010),10000);

        System.out.println("Waiting for server");
        System.out.println(s.isConnected());
        System.out.println("Type password friend to send objects");

        //Client write message

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        //Client has to type password to enter object sending mode
                        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                        String password = br.readLine();
                        //Serialized mode
                        if (password.equals("friend")) {
                            System.out.println("Type your message. Type break to exit serialized mode");
                            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                            Scanner console = new Scanner(System.in);
                            while (true) {
                                String message = console.nextLine();
                                if (message.contains("break")){
                                    break;
                                }
                                else {
                                    oos.writeObject(message);
                                    oos.flush();
                                }
                            }
                        }
                        //Normal mode
                        else {
                            PrintWriter pw = new PrintWriter(s.getOutputStream());
                            Scanner console = new Scanner(System.in);
                            while (true) {
                                String message = console.nextLine();
                                pw.println(message);
                                pw.flush();
                            }
                        }
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }

            }
        }).start();

        //Client read message

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = s.getInputStream();
                    InputStreamReader adapter = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(adapter);
                    String remoteMessage = br.readLine();
                    while (remoteMessage!=null){
                        System.out.println(remoteMessage);
                        remoteMessage = br.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

}
