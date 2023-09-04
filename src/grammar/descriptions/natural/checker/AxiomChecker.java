package grammar.descriptions.natural.checker;

import grammar.descriptions.natural.Rule;
import grammar.proof.MetaProof;

import java.util.List;

public class AxiomChecker implements RuleChecker {

    @Override
    public boolean check(final MetaProof root, final List<MetaProof> children) {
        return root.getProof().getContext().contains(root.getProof().getExpression())
                && children.isEmpty();
    }

    @Override
    public Rule getRule() {
        return Rule.AXIOM;
    }
}
