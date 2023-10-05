package grammar.descriptions.natural.checker;

import grammar.Expression;
import grammar.descriptions.RuleChecker;
import grammar.descriptions.natural.NaturalRule;
import grammar.operators.Operator;
import grammar.proof.MetaProof;

import java.util.List;

public class AndDecompositionRightChecker implements RuleChecker {
    @Override
    public boolean check(final MetaProof root,
                         final List<MetaProof> children) {
        MetaProof from = children.get(0);
        List<Expression> decomp = Expression.decomposition(
                from.getProof().getExpression(),
                Operator.AND);

        Expression right = decomp.get(1);

        return root.getProof().getExpression().equals(right)
                && root.getProof().getContext().equals(from.getProof().getContext())
                && children.size() == 1;
    }

    @Override
    public NaturalRule getRule() {
        return NaturalRule.AND_DECOMPOSITION_RIGHT;
    }
}
