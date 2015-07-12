

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by Master on 11-May-15.
 */
public class SimpleClient {
    public static void main(String[] args) throws IOException {

        final Socket s = new Socket();
        s.connect(new InetSocketAddress("localhost", 10010), 10000);

        System.out.println("Waiting for server");
        System.out.println(s.isConnected());

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

                //SimpleClient write message - Serialized mode

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
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


    }
}
