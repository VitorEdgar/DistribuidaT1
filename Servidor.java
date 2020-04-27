import java.io.IOException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class Servidor extends UnicastRemoteObject implements ServidorInterface {

    private static final long serialVersionUID = 1L;

    private static volatile ArrayList<RegistroRecurso> recursos = new ArrayList<>();


    protected Servidor() throws RemoteException {
    }

    public static void iniciar(InetAddress adress, String nick) throws IOException {
        System.out.println("Servidor");
        System.out.println(adress.getHostAddress());

        try {
            Naming.rebind("Servidor", new Servidor());
            System.out.println("Servidor is ready.");
        } catch (Exception e) {
            System.out.println("Servidor failed: " + e);
        }
        while (true){

        }
    }


    @Override
    public int registrar(String cliente,
                         String IPAdress,
                         HashMap<String, String> arquivos) throws RemoteException {
        arquivos.forEach( (key, value) -> {
            RegistroRecurso registroRecurso = new RegistroRecurso();
            registroRecurso.setIp(IPAdress);
            registroRecurso.setHash(value);
            registroRecurso.setNome(key);
            registroRecurso.setNomeCliente(cliente);
            recursos.add(registroRecurso);
        });
        return 0;
    }

    @Override
    public int ping(String IPAdress) throws RemoteException {
        return 0;
    }

    @Override
    public int solicitar(String nomeArquivo) throws RemoteException {
        return 0;
    }

    @Override
    public int sair(String IPAdress) throws RemoteException {
        return 0;
    }
}
