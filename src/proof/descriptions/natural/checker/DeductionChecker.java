package proof.descriptions.natural.checker;

import grammar.Expression;
import proof.descriptions.RuleChecker;
import proof.descriptions.natural.NaturalRule;
import grammar.operators.Operator;
import proof.MetaProof;

import java.util.List;

public class DeductionChecker implements RuleChecker {
    @Override
    public boolean check(MetaProof root, List<MetaProof> children) {
        MetaProof ded = children.get(0);

        List<Expression> decomposition = Expression.decomposition(
                root.getProof().getExpression(),
                Operator.IMPL);

        return root.getProof().getContext().merge(decomposition.get(0)).equals(ded.getProof().getContext())
                && ded.getProof().getExpression().equals(decomposition.get(1))
                && decomposition.size() == 2
                && children.size() == 1;
    }

    @Override
    public NaturalRule getRule() {
        return NaturalRule.DEDUCTION;
    }
}
