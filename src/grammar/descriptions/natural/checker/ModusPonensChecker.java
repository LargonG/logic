package grammar.descriptions.natural.checker;

import grammar.Expression;
import grammar.descriptions.RuleChecker;
import grammar.descriptions.natural.NaturalRule;
import grammar.operators.Operator;
import grammar.proof.MetaProof;

import java.util.List;

public class ModusPonensChecker implements RuleChecker {
    @Override
    public boolean check(MetaProof root, List<MetaProof> children) {
        MetaProof impl = children.get(0);
        MetaProof alpha = children.get(1);

        List<Expression> decomposition = Expression.decomposition(
                impl.getProof().getExpression(),
                Operator.IMPL);

        return decomposition.get(0).equals(alpha.getProof().getExpression())
                && decomposition.size() == 2
                && decomposition.get(1).equals(root.getProof().getExpression())
                && impl.getProof().getContext().equals(alpha.getProof().getContext())
                && root.getProof().getContext().equals(alpha.getProof().getContext())
                && children.size() == 2;
    }

    @Override
    public NaturalRule getRule() {
        return NaturalRule.MODUS_PONENS;
    }
}
