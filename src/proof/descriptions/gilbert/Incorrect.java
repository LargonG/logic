package proof.descriptions.gilbert;

/**
 * Proof is incorrect
 */
public class Incorrect extends GuilbertDescription {
    public Incorrect() {
        super(GuilbertRule.INCORRECT);
    }

    @Override
    public String toString() {
        return "Incorrect";
    }
}
