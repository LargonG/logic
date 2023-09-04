package grammar.descriptions.natural.checker;

import grammar.Expression;
import grammar.descriptions.natural.Rule;
import grammar.operators.Operator;
import grammar.proof.MetaProof;

import java.util.List;

public class ExcludedMiddleChecker implements RuleChecker {
    @Override
    public boolean check(MetaProof root, List<MetaProof> children) {
        MetaProof phi = children.get(0);
        MetaProof pci = children.get(1);
        MetaProof phiOrPci = children.get(2);

        List<Expression> decomp = Expression.decomposition(
                phiOrPci.getProof().getExpression(),
                Operator.OR);
        Expression phiExpr = decomp.get(0);
        Expression pciExpr = decomp.get(1);

        return phi.getProof().getContext().remove(phiExpr).equals(
                pci.getProof().getContext().remove(pciExpr))
                && phiOrPci.getProof().getContext().equals(root.getProof().getContext())
                && phiOrPci.getProof().getContext().equals(phi.getProof().getContext().remove(phiExpr))
                && phi.getProof().getExpression().equals(root.getProof().getExpression())
                && pci.getProof().getExpression().equals(root.getProof().getExpression())
                && phi.getProof().getContext().equals(root.getProof().getContext().add(phiExpr))
                && pci.getProof().getContext().equals(root.getProof().getContext().add(pciExpr))
                && children.size() == 3;
    }

    @Override
    public Rule getRule() {
        return Rule.EXCLUDED_MIDDLE_RULE;
    }
}
