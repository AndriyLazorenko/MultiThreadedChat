import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

/**
 * Created by Master on 11-May-15.
 */

//Реализовать передачу объектов
    // Реализовать выход из чата пользователя с удалением соответствующих потоков
    // Разнести по разным классам и пакаджам

public class MyServer {

    public static void main(String[] args) throws IOException {
        Socket client;
        ClientsContainer container = new ClientsContainer();
        Properties properties = new Properties();
        File file = new File("G:\\ServerProperties.txt");
        FileReader fr = new FileReader(file);
        properties.load(fr);
        ServerSocket ss = new ServerSocket(Integer.parseInt(properties.getProperty("port")));

        while (container.getNumberOfClients()<Integer.parseInt(properties.getProperty("maxUsersSize"))) {
            client = ss.accept();
            container.putClient(client,client.getOutputStream());
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
        ClientsContainer observers;
        BufferedReader bf;
        int port;
        String ip;

        public ListenerThread (ClientsContainer clients, InputStream is, String ip, int port){
            this.observers = clients;
            this.bf = new BufferedReader(new InputStreamReader(is));
            this.ip = ip;
            this.port = port;
        }
        @Override
        public void run(){

            while (true){
                try {
                    String s = bf.readLine();
                    if (s != null) {
                        String message = ip + ":" + ":" + port + " -> " + s;
                        observers.sendMessage(message);
                        System.out.println(message);
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                    Socket forRemoval = null;
                    for (Socket s:observers.clientSet()){
                        if (s.getLocalPort()==port){
                            forRemoval=s;
                        }
                    }
                    observers.removeClient(forRemoval);
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

        }
    }
//    class SpeakerThread implements Runnable{
//        ClientsContainer observer;
//        BufferedReader bf;
//        int port;
//        String ip;
//
//        public SpeakerThread (ClientsContainer clients, InputStream is, String ip, int port){
//            this.observer = clients;
//            this.bf = new BufferedReader(new InputStreamReader(is));
//            this.ip = ip;
//            this.port = port;
//        }
//
//        @Override
//        public void run(){
//            while (true){
//            try {
//                String s = bf.readLine();
//                if(s != null){
//                    String message = ip + ":" + ":" + port + " -> " + s;
//                    observer.sendMessage(message);
//                    System.out.println(message);
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            }
//        }
//    }

    class ClientsContainer {
        private File file = new File("G://ChatOutput.txt");
//        private List<PrintWriter> clients = new LinkedList<>();
        private Map<Socket,PrintWriter> clients = new LinkedHashMap<>();
        private int numberOfClients=0;

        public Set<Socket> clientSet (){
            return clients.keySet();
        }
        public int getNumberOfClients() {
            setNumberOfClients(clients.size());
            return numberOfClients;
        }

        public void setNumberOfClients(int numberOfClients) {
            this.numberOfClients = numberOfClients;
        }

        public void putClient (Socket client, OutputStream os) {
            this.clients.put(client, new PrintWriter(os));
        }

        public void removeClient (Socket client) {
            this.clients.remove(client);
        }
        public synchronized void sendMessage(String message){
            for (PrintWriter pw : clients.values()){
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
