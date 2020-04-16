
public class Main{

    public static void main(String[] args){
        System.out.println(args[0]);
        if(args[0].equalsIgnoreCase("cliente")){
            System.out.println("Cliente");
        }else{
            System.out.println("Servidor");
        };
    }

}