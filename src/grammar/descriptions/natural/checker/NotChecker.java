package grammar.descriptions.natural.checker;

import grammar.Expression;
import grammar.Nil;
import grammar.descriptions.natural.Rule;
import grammar.operators.Operator;
import grammar.proof.MetaProof;

import java.util.List;

public class NotChecker implements RuleChecker {
    @Override
    public boolean check(MetaProof root, List<MetaProof> children) {
        MetaProof from = children.get(0);

        Expression not = Expression.create(Operator.IMPL, root.getProof().getExpression(), Nil.getInstance());
        return from.getProof().getExpression().equals(Nil.getInstance())
                && from.getProof().getContext().contains(not)
                && root.getProof().getContext().equals(from.getProof().getContext().remove(not))
                && children.size() == 1;
    }

    @Override
    public Rule getRule() {
        return Rule.NOT;
    }
}
