package grammar.descriptions.natural.checker;

import grammar.Expression;
import grammar.descriptions.natural.Rule;
import grammar.operators.Operator;
import grammar.proof.MetaProof;

import java.util.List;

public class DeductionChecker implements RuleChecker {
    @Override
    public boolean check(MetaProof root, List<MetaProof> children) {
        MetaProof ded = children.get(0);

        List<Expression> decomposition = Expression.decomposition(
                root.getProof().getExpression(),
                Operator.IMPL);

        return root.getProof().getContext().add(decomposition.get(0)).equals(ded.getProof().getContext())
                && ded.getProof().getExpression().equals(decomposition.get(1))
                && decomposition.size() == 2
                && children.size() == 1;
    }

    @Override
    public Rule getRule() {
        return Rule.DEDUCTION;
    }
}
