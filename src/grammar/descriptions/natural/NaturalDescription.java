package grammar.descriptions.natural;

import grammar.descriptions.Description;
import grammar.proof.NProof;

import java.util.Collections;
import java.util.List;

public class NaturalDescription extends Description {
    public NaturalDescription(final NaturalRule naturalRule,
                              final List<NProof> links) {
        super(naturalRule, links);
    }

    public NaturalDescription(final NaturalRule naturalRule,
                              final NProof... links) {
        super(naturalRule, links);
    }

    public NaturalDescription() {
        super(NaturalRule.AXIOM, Collections.emptyList());
    }
}
