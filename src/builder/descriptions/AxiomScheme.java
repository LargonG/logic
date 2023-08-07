package builder.descriptions;

public class AxiomScheme implements Description {
    public final int id;

    public AxiomScheme(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Ax. sch. " + (id + 1);
    }
}
