import java.rmi.RemoteException;
import java.util.HashMap;

public interface ServidorInterface {

    public int registrar(String nomeCliente, String IPAdress, HashMap<String, String> arquivos) throws RemoteException;

    public int ping(String IPAdress) throws RemoteException;

    public int solicitar(String nomeArquivo) throws RemoteException;

    public int sair(String IPAdress) throws RemoteException;
}
