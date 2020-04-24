package src;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Cliente {
    public static void iniciar(InetAddress grupo, String nick, MulticastSocket socket) throws IOException {
        System.out.println("Cliente");
        System.out.println(grupo.getHostAddress());

        ArrayList<String> arquivosDisponiveis = getArquivosDisponiveis();

        InetAddress host;

        byte[] saida = new byte[1024];
        saida = ("[" + nick + "] " + " /all/ Ola").getBytes();
        DatagramPacket pacote = new DatagramPacket(saida, saida.length, grupo, 5000);
        socket.send(pacote);
        while (true) {
            try {
                byte[] entrada = new byte[1024];
                pacote = new DatagramPacket(entrada, entrada.length);
                socket.setSoTimeout(500);
                socket.receive(pacote);
                String recebido = new String(pacote.getData(), 0, pacote.getLength());

                String vars[] = recebido.split("/");
                try {
                    System.out.println("Name: " + vars[1]);
                    if (vars[1].equals(nick)) {
                        System.out.println("Received: " + recebido);
                        if (recebido.contains("Host")) {
                            host = pacote.getAddress();

                            System.out.println("Host=" + host.toString());
                        }
                        if ("fim".equals(recebido))
                            break;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            } catch (IOException e) {
            }
        }
    }

    private static ArrayList<String> getArquivosDisponiveis() {
        ArrayList<String> arquivosDisponiveis = new ArrayList<>();

        try (Stream<Path> walk = Files.walk(Paths.get("disponiveis"))) {

            walk.filter(Files::isRegularFile).forEach(arquivo -> {
                        arquivosDisponiveis.add(arquivo.getFileName().toString());
                        try {
                            arquivosDisponiveis.add(geraHash(arquivo.toString()));
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
}
