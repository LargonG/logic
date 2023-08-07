package builder.descriptions;

public class Hypothesis implements Description {
    public final int id;

    public Hypothesis(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Hyp. " + (id + 1);
    }
}
