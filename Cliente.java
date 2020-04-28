import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

public class Cliente extends UnicastRemoteObject implements ClienteInterface {

    private static final long serialVersionUID = 1L;

    private static volatile HashMap<String,String>  arquivosDisponiveis;
    private static volatile Boolean ligado;

    protected Cliente() throws RemoteException {
    }

    public static void iniciar(InetAddress grupo, String nick) throws IOException {
        ligado = Boolean.TRUE;
        System.out.println("Cliente");
        System.out.println(grupo.getHostAddress());
        Scanner scanner = new Scanner(System.in);

        Cliente cliente = new Cliente();

        try {
            Naming.rebind("Cliente", cliente);
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

        String ip = null;

        try {
            ip = servidor.registrar(nick,arquivosDisponiveis, cliente);
            System.out.println("Call to Servidor...");
        } catch (RemoteException e) {
            e.printStackTrace();
        }


        Thread pingThread = new Thread(new Ping(servidor,  nick));
        pingThread.start();



        while (ligado) {
            System.out.print("Solicitar arquivos Disponiveis (S), Solicitar Recurso (SR) ou Sair (Q): ");
            String acao = scanner.next();
            if(!ligado) break;
            switch (acao) {
                case "S":
                    try {
                        List<String> recursos = servidor.solicitar();
                        recursos.forEach(System.out::println);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                    break;
                case "SR":
                    System.out.print("Digitar nome do arquivo: ");
                    String arquivo = scanner.next();
                    try {
                        ClienteInterface peer = servidor.solicitarRecurso(arquivo);
                        if(peer == null){
                            System.out.println("Arquivo não existe");
                        }else {
                            try{
                                peer.solicitarRecurso(arquivo,  cliente);
                            } catch (Exception e) {
                                System.out.println("Cliente failed: ");
                                e.printStackTrace();
                            }
                        }
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                    break;
                case "Q":
                    servidor.sair(grupo.getHostName());
                    break;
                default:
                    System.out.print("'" + acao + "' não é uma ação possivel");
                    break;
            }
        }
        pingThread.interrupt();
        System.exit(1);
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
        System.out.println("Peer removido pelo host");
        ligado = Boolean.FALSE;
        return 0;
    }

    @Override
    public int solicitarRecurso(String nome, ClienteInterface cliente) throws RemoteException {
        System.out.println("Enviando arquivo");
        try {
            File file = new File("disponiveis/" + nome);
            FileInputStream fis = new FileInputStream(file);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];

            try {
                for (int readNum; (readNum = fis.read(buf)) != -1;) {
                    bos.write(buf, 0, readNum); //no doubt here is 0

                    System.out.println("read " + readNum + " bytes,");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] bytes = bos.toByteArray();
            cliente.receberArquivo(nome, bytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int receberArquivo(String nome, byte[] recurso) throws RemoteException {
        System.out.println("Recebendo arquivo");
        System.out.println(recurso.length);
        File someFile = new File(nome);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(someFile);
            fos.write(recurso);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
