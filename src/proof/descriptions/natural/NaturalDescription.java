package proof.descriptions.natural;

import proof.descriptions.Description;
import proof.NProof;

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
