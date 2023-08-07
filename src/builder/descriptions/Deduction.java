package builder.descriptions;

public class Deduction implements Description {
    public final int id;

    public Deduction(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Ded. " + (id + 1);
    }
}
