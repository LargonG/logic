package proof.descriptions.natural.checker;

import grammar.Expression;
import proof.descriptions.RuleChecker;
import proof.descriptions.natural.NaturalRule;
import grammar.operators.Operator;
import proof.MetaProof;

import java.util.List;

public class AndDecompositionLeftChecker implements RuleChecker {
    @Override
    public boolean check(final MetaProof root,
                         final List<MetaProof> children) {
        MetaProof from = children.get(0);
        List<Expression> decomp = Expression.decomposition(
                from.getProof().getExpression(),
                Operator.AND);

        Expression left = decomp.get(0);

        return root.getProof().getExpression().equals(left)
                && root.getProof().getContext().equals(from.getProof().getContext())
                && children.size() == 1;
    }

    @Override
    public NaturalRule getRule() {
        return NaturalRule.AND_DECOMPOSITION_LEFT;
    }
}
