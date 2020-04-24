package src;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Main {

    public static void main(String[] args) throws IOException {

        // Conectando no Grupo
        MulticastSocket socket = new MulticastSocket(5000);
        InetAddress grupo = InetAddress.getByName(args[1]);
        socket.joinGroup(grupo);
        String nick = args[2];

        if (args[0].equalsIgnoreCase("cliente")) {
            Cliente.iniciar(grupo, nick, socket);
        } else {
            Servidor.iniciar(grupo, nick, socket);
        }
    }
}