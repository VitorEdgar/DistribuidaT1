import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        // Conectando no Grupo
        MulticastSocket socket = new MulticastSocket(5000);
        InetAddress grupo = InetAddress.getByName(args[1]);
        socket.joinGroup(grupo);
        String nick = args[2];

        DatagramPacket pacote;

        if (args[0].equalsIgnoreCase("cliente")) {
            System.out.println("Cliente");
            System.out.println(grupo.getHostAddress());

            byte[] saida = new byte[1024];
            saida = ("[" + nick + "] " + " /all/ Ola").getBytes();
            pacote = new DatagramPacket(saida, saida.length, grupo, 5000);
            socket.send(pacote);
            while(true){
                try {
                    byte[] entrada = new byte[1024];
                    pacote = new DatagramPacket(entrada,entrada.length);
                    socket.setSoTimeout(500);
                    socket.receive(pacote);
                    String recebido = new String(pacote.getData(),0,pacote.getLength());
                    
                    String vars[] = recebido.split("/");
                    try {
                        System.out.println("Name: " + vars[1]);
                        if (vars[1].equals(nick)) {
                            System.out.println("Received: " + recebido);
                            if ("fim".equals(recebido))
                                break;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                } catch (IOException e){
                }
            }
        } else {
            System.out.println("Servidor");
            System.out.println(grupo.getHostAddress());

            while(true){
                try {
                    byte[] entrada = new byte[1024];
                    pacote = new DatagramPacket(entrada,entrada.length);
                    socket.setSoTimeout(500);
                    socket.receive(pacote);
                    String recebido = new String(pacote.getData(),0,pacote.getLength());
                    
                    String vars[] = recebido.split("/");
                    try {
                        System.out.println("Name: " + vars[1]);
                        if(vars[1].equalsIgnoreCase("all")){
                            System.out.println("Recebido " + vars[0] + " - " + pacote.getAddress());
                        }
                        if (vars[1].equals(nick)) {
                            System.out.println("Received: " + recebido);
                            if ("fim".equals(recebido))
                                break;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                    }
                } catch (IOException e){
                }
            }
        }
        ;
    }

}