package Utils;

public class Pedido {
    private final int id;
    private final String source;

    public Pedido(int id, String source) {
        this.id = id;
        this.source = source;
    }

    // getters
    public int getId() { return id; }
    public String getSource() { return source; }
}
