public class RegistroCliente {


    private String ip;
    private Long ultimaInteracao;
    private ClienteInterface cliente;

    public RegistroCliente() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getUltimaInteracao() {
        return ultimaInteracao;
    }

    public void setUltimaInteracao(Long ultimaInteracao) {
        this.ultimaInteracao = ultimaInteracao;
    }

    public ClienteInterface getCliente() {
        return cliente;
    }

    public void setCliente(ClienteInterface cliente) {
        this.cliente = cliente;
    }
}
