package proof.descriptions.natural.checker;

import grammar.Expression;
import proof.descriptions.RuleChecker;
import proof.descriptions.natural.NaturalRule;
import grammar.operators.Operator;
import proof.MetaProof;

import java.util.List;

public class AndCompositionChecker implements RuleChecker {
    @Override
    public boolean check(MetaProof root, List<MetaProof> children) {
        List<Expression> decomposition = Expression.decomposition(
                root.getProof().getExpression(),
                Operator.AND);

        MetaProof left = children.get(0);
        MetaProof right = children.get(1);

        Expression leftExpr = decomposition.get(0);
        Expression rightExpr = decomposition.get(1);


        return left.getProof().getExpression().equals(leftExpr)
                && right.getProof().getExpression().equals(rightExpr)
                && left.getProof().getContext().equals(right.getProof().getContext())
                && root.getProof().getContext().equals(left.getProof().getContext())
                && children.size() == 2;
    }

    @Override
    public NaturalRule getRule() {
        return NaturalRule.AND_COMPOSITION;
    }
}
