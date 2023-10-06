package proof.descriptions;

import proof.MetaProof;

import java.util.Arrays;
import java.util.List;

/**
 * Description for the proof
 */
public abstract class Description {
    private final Rule rule;
    private final List<? extends MetaProof> links;

    public Description(final Rule rule,
                       final List<? extends MetaProof> links) {
        if (rule.getCount() != links.size()) {
            throw new IllegalArgumentException(
                    "Not enough links for the current rule: " + rule
                            + "\nlinks: " + links
            );
        }

        this.rule = rule;
        this.links = links;
    }

    @SafeVarargs
    public Description(final Rule rule,
                       final MetaProof... links) {
        this(rule, Arrays.asList(links));
    }


    public <T extends MetaProof> List<T> getLinks() {
        return (List<T>) links;
    }

    public Rule getRule() {
        return rule;
    }

    @Override
    public String toString() {
        return rule.toString();
    }
}
