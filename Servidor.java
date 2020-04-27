import java.io.IOException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Servidor extends UnicastRemoteObject implements ServidorInterface {

    private static final long serialVersionUID = 1L;

    private static volatile ArrayList<RegistroRecurso> recursos;


    protected Servidor() throws RemoteException {
    }

    public static void iniciar(InetAddress adress, String nick) throws IOException {
        System.out.println("Servidor");
        System.out.println(adress.getHostAddress());
        recursos = new ArrayList<>();

        try {
            Naming.rebind("Servidor", new Servidor());
            System.out.println("Servidor is ready.");
        } catch (Exception e) {
            System.out.println("Servidor failed: " + e);
        }
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int registrar(String cliente,
                         String IPAdress,
                         HashMap<String, String> arquivos) throws RemoteException {
        System.out.println("Registrando recursos de " + IPAdress);
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
    public List<String> solicitar(String nomeArquivo) throws RemoteException {
        System.out.println("Recursos Solicitados");
        return recursos.stream()
                .map(recurso -> recurso.getNome() +" - "+ recurso.getIp())
                .collect(Collectors.toList());
    }

    @Override
    public int sair(String IPAdress) throws RemoteException {
        return 0;
    }
}
