

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
        s.connect(new InetSocketAddress("localhost", 10010), 10000);

        System.out.println("Waiting for server");
        System.out.println(s.isConnected());
        System.out.println("Type 'default', 'serialized' or 'XML' mode to choose a method of sending messages");

        //Client has to type keyword to enter object sending mode

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        final String keyword = br.readLine();

        //Client read message

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = s.getInputStream();
                    InputStreamReader adapter = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(adapter);
                    String remoteMessage = br.readLine();
                    while (remoteMessage != null) {
                        System.out.println(remoteMessage);
                        remoteMessage = br.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        switch (keyword) {
            case "serialized": {

                //Client write message - Serialized mode

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Common block - sending the keyword to server
                            PrintWriter pw = new PrintWriter(s.getOutputStream());
                            pw.print(keyword);
                            pw.flush();
                            //Block dependent on keyword
                                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                                Scanner console = new Scanner(System.in);
                                while (s.isConnected()) {
                                    String message = console.nextLine();
                                    oos.writeObject(message);
                                    oos.flush();
                                }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            }
            case "XML": {

                //XML mode

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Common block - sending the keyword to server
                            PrintWriter pw = new PrintWriter(s.getOutputStream());
                            pw.print(keyword);
                            pw.flush();
                            //Block dependent on keyword

                            while (true) {

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            }
            default: {

                //Normal mode

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Common block - sending the keyword to server
                            PrintWriter pw = new PrintWriter(s.getOutputStream());
                            pw.print(keyword);
                            pw.flush();
                            //
                            Scanner console = new Scanner(System.in);
                            while (s.isConnected()) {
                                String message = console.nextLine();
                                pw.println(message);
                                pw.flush();
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }).start();
                break;
            }
        }

    }
}
