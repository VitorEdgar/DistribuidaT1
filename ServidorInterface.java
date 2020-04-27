import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

public interface ServidorInterface extends Remote {

    public int registrar(String nomeCliente, String IPAdress, HashMap<String, String> arquivos) throws RemoteException;

    public int ping(String IPAdress) throws RemoteException;

    public List<String> solicitar(String nomeArquivo) throws RemoteException;

    public int sair(String IPAdress) throws RemoteException;
}
