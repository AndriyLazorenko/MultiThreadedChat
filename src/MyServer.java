import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Master on 11-May-15.
 */
public class MyServer {

    public static void main(String[] args) throws IOException {
        Socket client;
        ServerSocket ss = new ServerSocket(10010);
        ClientsContainer container = new ClientsContainer();

        System.out.println("Waiting for client");

        while (true) {
            client = ss.accept();
            container.addClient(client.getOutputStream());
            ListenerThread lth = new ListenerThread(container,client.getInputStream(),client.getInetAddress().toString(),client.getPort());
            Thread thi = new Thread(lth);
            thi.start();

            String message = String.format("New client connection ip %s, port %s\n",
                    client.getInetAddress(),
                    client.getPort());
            System.out.println(message);
        }
    }
}

    class ListenerThread implements Runnable{
        ClientsContainer observer;
        BufferedReader bf;
        int port;
        String ip;

        public ListenerThread (ClientsContainer clients, InputStream is, String ip, int port){
            this.observer = clients;
            this.bf = new BufferedReader(new InputStreamReader(is));
            this.ip = ip;
            this.port = port;
        }
        @Override
        public void run(){

            while (true){
                try {
                    String s = bf.readLine();
                    if(s != null){
                        String message = ip + ":" + ":" + port + " -> " + s;
                        observer.sendMessage(message);
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

        }
    }
    class SpeakerThread implements Runnable{
        ClientsContainer observer;
        BufferedReader bf;
        int port;
        String ip;

        public SpeakerThread (ClientsContainer clients, InputStream is, String ip, int port){
            this.observer = clients;
            this.bf = new BufferedReader(new InputStreamReader(is));
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run(){
            while (true){
            try {
                String s = bf.readLine();
                if(s != null){
                    String message = ip + ":" + ":" + port + " -> " + s;
                    observer.sendMessage(message);
                    System.out.println(message);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        }
    }

    class ClientsContainer {
        private File file = new File("G://ChatOutput.txt");
        public List<PrintWriter> getClients() {
            return clients;
        }

        private List<PrintWriter> clients = new LinkedList<>();

        public void addClient (OutputStream os) {
            this.clients.add(new PrintWriter(os));
        }
        public synchronized void sendMessage(String message){
            for (PrintWriter pw : clients){
                pw.println(message);
                pw.flush();
            }
            toFile(message);
        }
        private void toFile(String s) {
            FileWriter fw;
            try {
                fw = new FileWriter(file,true);
                fw.write(s+"\n");
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
