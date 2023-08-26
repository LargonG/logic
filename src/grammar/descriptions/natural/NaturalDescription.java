package grammar.descriptions.natural;

import grammar.descriptions.Description;
import grammar.proof.NProof;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NaturalDescription implements Description {
    private final Rule rule;
    private final List<NProof> links;

    public NaturalDescription(final Rule rule,
                              final List<NProof> links) {
        if (rule.getCount() != links.size()) {
            throw new IllegalArgumentException(
                    "Not enough links for the current rule: " + rule
                    + "\nlinks: " + links
            );
        }

        this.rule = rule;
        this.links = links;
    }

    public NaturalDescription(final Rule rule,
                              final NProof... links) {
        this(rule, Arrays.asList(links));
    }

    public NaturalDescription() {
        this(Rule.AXIOM, Collections.emptyList());
    }

    @Override
    public List<NProof> getLinks() {
        return links;
    }

    @Override
    public String toString() {
        return rule.toString();
    }
}
