import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.stream.Stream;

public class Cliente extends UnicastRemoteObject implements ClienteInterface {

    private static final long serialVersionUID = 1L;

    protected Cliente() throws RemoteException {
    }

    public static void iniciar(InetAddress grupo, String nick) throws IOException {
        System.out.println("Cliente");
        System.out.println(grupo.getHostAddress());
        Scanner scanner = new Scanner(System.in);

        try {
            Naming.rebind(nick, new Cliente());
            System.out.println("Cliente is ready.");
        } catch (Exception e) {
            System.out.println("Cliente failed: " + e);
        }

        String remoteHostName = grupo.getHostAddress();
        String connectLocation = "//" + remoteHostName + "/Servidor";

        ServidorInterface servidor = null;
        try {
            System.out.println("Connecting to Servidor at : " + connectLocation);
            servidor = (ServidorInterface) Naming.lookup(connectLocation);
        } catch (Exception e) {
            System.out.println("Cliente failed: ");
            e.printStackTrace();
        }

        HashMap<String,String> arquivosDisponiveis = getArquivosDisponiveis();

        InetAddress ip = InetAddress.getLocalHost();

        try {
            servidor.registrar(nick,ip.toString(),arquivosDisponiveis);
            System.out.println("Call to Servidor...");
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        while (true) {
            System.out.print("Solicitar arquivos (S) ou Sair(Q): ");
            String acao = scanner.next();
            switch (acao) {
                case "S":
                    System.out.print("Digitar nome do arquivo(Vazio para retornar disponiveis): ");
                    String arquivo = scanner.next();
                    servidor.solicitar(arquivo);
                    break;
                case "Q":
                    servidor.sair(grupo.getHostName());
                    break;
                default:
                    System.out.print("'" + acao + "' não é uma ação possivel");
                    break;
            }
        }
    }

    private static HashMap<String,String> getArquivosDisponiveis() {
        HashMap<String, String> arquivosDisponiveis = new HashMap<>();

        try (Stream<Path> walk = Files.walk(Paths.get("disponiveis"))) {

            walk.filter(Files::isRegularFile).forEach(arquivo -> {
                        try {
                            arquivosDisponiveis.put(arquivo.getFileName().toString(),geraHash(arquivo.toString()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );

        } catch (IOException e) {
            e.printStackTrace();
        }

        return arquivosDisponiveis;

    }

    private static String geraHash(String arquivo) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(arquivo), md)) {
            while (dis.read() != -1) ;
            md = dis.getMessageDigest();
        }

        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }

        return result.toString();

    }

    @Override
    public int remover() throws RemoteException {
        return 0;
    }
}
