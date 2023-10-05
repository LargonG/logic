package grammar.descriptions.natural.checker;

import grammar.descriptions.RuleChecker;
import grammar.descriptions.natural.NaturalRule;
import grammar.proof.MetaProof;

import java.util.List;

public class AxiomChecker implements RuleChecker {

    @Override
    public boolean check(final MetaProof root, final List<MetaProof> children) {
        return root.getProof().getContext().contains(root.getProof().getExpression())
                && children.isEmpty();
    }

    @Override
    public NaturalRule getRule() {
        return NaturalRule.AXIOM;
    }
}
