import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Servidor extends UnicastRemoteObject implements ServidorInterface {

    private static final long serialVersionUID = 1L;

    private static volatile ArrayList<RegistroRecurso> recursos;
    private static volatile HashMap<String, RegistroCliente> clientes;
    private static volatile String hostname;


    protected Servidor() throws RemoteException {
    }

    public static void iniciar(InetAddress adress, String nick) throws IOException {
        System.out.println("Servidor");
        System.out.println(adress.getHostAddress());
        recursos = new ArrayList<>();
        clientes = new HashMap<>();
        hostname = adress.getHostAddress();


        try {
            Naming.rebind("Servidor", new Servidor());
            System.out.println("Servidor is ready.");
        } catch (Exception e) {
            System.out.println("Servidor failed: " + e);
        }
        while (true) {
            try {
                ArrayList<String> eliminados = new ArrayList<>();
                clientes.entrySet().stream().forEach(entry -> {
                    if (System.currentTimeMillis() - entry.getValue().getUltimaInteracao() > 10000) {
                        eliminados.add(entry.getKey());
                        System.out.println("Cliente " + entry.getKey() + " encerrado por timeout");
                    }
                });
                if (!eliminados.isEmpty()) {
                    List<RegistroRecurso> recursosEliminados = recursos.stream()
                            .filter(recurso -> eliminados.contains(recurso.getNomeCliente()))
                            .collect(Collectors.toList());
                    eliminados.forEach(eliminado -> clientes.remove(eliminado));
                    recursosEliminados.forEach(eliminado ->
                            recursos.removeIf(recurso ->
                                    recurso.getNomeCliente().equalsIgnoreCase(eliminado.getNomeCliente())
                            )
                    );
                }
            } catch (ConcurrentModificationException e) {
                System.out.println("Erro ao eliminar: " + e);
            }
        }
    }


    @Override
    public String registrar(String nomeCliente,
                            HashMap<String, String> arquivos) throws RemoteException {
        System.out.println("Registrando recursos de " + nomeCliente);

        try {
            String IPAdress = getClientHost();

            System.out.println(IPAdress);
            arquivos.forEach((key, value) -> {
                RegistroRecurso registroRecurso = new RegistroRecurso();
                registroRecurso.setIp(IPAdress);
                registroRecurso.setHash(value);
                registroRecurso.setNome(key);
                registroRecurso.setNomeCliente(nomeCliente);
                recursos.add(registroRecurso);
            });
            RegistroCliente cliente = new RegistroCliente();
            cliente.setIp(IPAdress);
            cliente.setNome(nomeCliente);
            cliente.setUltimaInteracao(System.currentTimeMillis());
            clientes.put(nomeCliente, cliente);
            return IPAdress;
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public int ping(String nick) throws RemoteException {
        System.out.println("Peer " + nick + " Ping");
        RegistroCliente cliente = clientes.get(nick);
        cliente.setUltimaInteracao(System.currentTimeMillis());
        return 0;
    }

    @Override
    public List<String> solicitar() throws RemoteException {
        System.out.println("Recursos Solicitados");
        return recursos.stream()
                .map(recurso -> recurso.getNome() + " - " + recurso.getHash() + " - " + recurso.getNomeCliente())
                .collect(Collectors.toList());

    }

    @Override
    public ClienteInterface solicitarRecurso(String nomeArquivo) throws RemoteException {
        System.out.println("Recurso Solicitado " + nomeArquivo);
        String nome = recursos.stream()
                .filter(recurso -> recurso.getNome().equalsIgnoreCase(nomeArquivo))
                .map(RegistroRecurso::getIp)
                .findFirst()
                .orElse(null);

        try {
            Registry reg = LocateRegistry.getRegistry(nome, 1099);
            System.out.println(reg);
            ClienteInterface cli = (ClienteInterface) reg.lookup("Cliente");
            System.out.println(cli.toString());
            cli.remover();
            return cli;
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int sair(String IPAdress) throws RemoteException {
        return 0;
    }
}
