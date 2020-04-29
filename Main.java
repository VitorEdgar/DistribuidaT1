import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {

    public static void main(String[] args) throws IOException {

        if(args.length < 4){
            System.out.println("Usage: java Main <cliente/servidor> <machine> <machine> <nickname>");
            System.exit(1);
        }
        InetAddress grupo = InetAddress.getByName(args[1]);
        InetAddress grupo2 = InetAddress.getByName(args[2]);
        String nick = args[3];

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