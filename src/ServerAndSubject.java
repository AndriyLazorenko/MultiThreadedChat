import java.net.Socket;

/**
 * Created by Master on 11-May-15.
 */
public interface ServerAndSubject {
    public void registerClient(ClientAndObserver c);
    public void removeClient(ClientAndObserver c);
    public void notifyClients();
    public void messageReceived();
}
