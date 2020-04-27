import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClienteInterface extends Remote {

    public int remover() throws RemoteException;

}
