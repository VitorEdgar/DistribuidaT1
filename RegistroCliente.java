public class RegistroCliente {

    private String nome;
    private String ip;
    private Long ultimaInteracao;
    private ClienteInterface cliente;

    public RegistroCliente() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
