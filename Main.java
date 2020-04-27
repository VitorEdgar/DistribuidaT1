import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {

    public static void main(String[] args) throws IOException {

        if(args.length < 3){
            System.out.println("Usage: java Main <cliente/servidor> <machine> <nickname>");
            System.exit(1);
        }
        InetAddress grupo = InetAddress.getByName(args[1]);
        String nick = args[2];

        try {
            System.setProperty("java.rmi.server.hostname", args[1]);
            LocateRegistry.createRegistry(1099);
            System.out.println("java RMI registry created.");
        } catch (RemoteException e) {
            System.out.println("java RMI registry already exists.");
        }

        if (args[0].equalsIgnoreCase("cliente")) {
            Cliente.iniciar(grupo, nick);
        } else {
            Servidor.iniciar(grupo, nick);
        }
    }
}