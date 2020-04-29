import java.rmi.RemoteException;

public class Ping implements Runnable {

    private final ServidorInterface servidor;
    private final String nick;

    public Ping(ServidorInterface servidor, String nick) {
        this.servidor = servidor;
        this.nick = nick;
    }

    @Override
    public void run() {
        while (true) {
            try {
                servidor.ping(nick);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
