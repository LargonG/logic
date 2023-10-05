package grammar.descriptions.gilbert;

import grammar.descriptions.Description;
import grammar.descriptions.Rule;
import grammar.proof.GProof;
import grammar.proof.MetaProof;

import java.util.List;

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
