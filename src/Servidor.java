package src;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Servidor {
    public static void iniciar(InetAddress grupo, String nick, MulticastSocket socket) throws IOException {
        System.out.println("Servidor");
        System.out.println(grupo.getHostAddress());

        while (true) {
            try {
                byte[] entrada = new byte[1024];
                DatagramPacket pacote = new DatagramPacket(entrada, entrada.length);
                socket.setSoTimeout(500);
                socket.receive(pacote);
                String recebido = new String(pacote.getData(), 0, pacote.getLength());

                String vars[] = recebido.split("/");
                try {
                    System.out.println("Name: " + vars[1]);
                    if (vars[1].equalsIgnoreCase("all")) {
                        System.out.println("Recebido " + vars[0] + " - " + pacote.getAddress());

                        byte[] saida = new byte[1024];
                        saida = ("[" + nick + "] "
                                + " /"
                                + vars[0].replace("[", "").replace("]", "").trim()
                                + "/ Host").getBytes();

                        pacote = new DatagramPacket(saida, saida.length, grupo, 5000);
                        socket.send(pacote);

                    }
                    if (vars[1].equals(nick)) {
                        System.out.println("Received: " + recebido);
                        if ("fim".equals(recebido))
                            break;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                }
            } catch (IOException e) {
            }
        }
    }
}
