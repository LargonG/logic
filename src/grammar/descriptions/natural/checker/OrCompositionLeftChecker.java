package grammar.descriptions.natural.checker;

import grammar.Expression;
import grammar.descriptions.RuleChecker;
import grammar.descriptions.natural.NaturalRule;
import grammar.operators.Operator;
import grammar.proof.MetaProof;

import java.util.List;

public class OrCompositionLeftChecker implements RuleChecker {
    @Override
    public boolean check(final MetaProof root,
                         final List<MetaProof> children) {
        MetaProof from = children.get(0);
        List<Expression> decomp = Expression.decomposition(
                root.getProof().getExpression(),
                Operator.OR);

        Expression left = decomp.get(0);

        return from.getProof().getExpression().equals(left)
                && root.getProof().getContext().equals(from.getProof().getContext())
                && children.size() == 1;
    }

    @Override
    public NaturalRule getRule() {
        return NaturalRule.OR_COMPOSITION_LEFT;
    }
}
