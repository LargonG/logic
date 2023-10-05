package grammar.predicates.quantifiers;

import grammar.BinaryOperator;
import grammar.Expression;
import grammar.predicates.arithmetic.Letter;
import grammar.proof.GProof;
import grammar.proof.builder.GProofBuilder;
import grammar.proof.context.ImmutableContext;
import util.Renamer;

import java.util.*;

public class ForAll extends Quantifier {
    public ForAll(Letter letter, Expression expression) {
        super(letter, expression, "@");
    }

    @Override
    public Expression paste(Map<String, Expression> values) {
        Expression newExpr = expression.paste(values);
        if (newExpr != expression) {
            return new ForAll(letter, newExpr);
        }
        return this;
    }

    @Override
    public Expression toNormalForm() {
        Expression newExpr = expression.toNormalForm();
        if (newExpr != expression) {
            return new ForAll(letter, newExpr);
        }
        return this;
    }

    @Override
    public Expression renameLetter(String oldName, String newName) {
        if (!letter.getName().equals(oldName)) {
            Expression newExpr = expression.renameLetter(oldName, newName);
            if (newExpr != expression) {
                return new ForAll(letter, newExpr);
            }
        }

        return this;
    }

    @Override
    public PreliminaryFormStep preliminaryFormStep(Renamer renamer, boolean restruct, boolean operations) {
        if (!restruct) {
            GProof aa = GProofBuilder.aa(this, ImmutableContext.empty());
            return new PreliminaryFormStep(null, true, this,
                    aa, aa);
        }

        if (!operations) {
            PreliminaryFormStep step = expression.preliminaryFormStep(renamer, true, false);
            GProof from = GProofBuilder.mergeForAll(Collections.singletonList(step.from), Collections.singletonList(letter));
            GProof to = GProofBuilder.mergeForAll(Collections.singletonList(step.to), Collections.singletonList(letter));
            return new PreliminaryFormStep(null, true,
                    step.expression == expression ? this : new ForAll(letter, step.expression), from, to);
        }

        // Иначе мы пришли к изменению

        String newName = renamer.getNext();
        Quantifier q = this;
        GProof from = GProofBuilder.renameForAll(Collections.emptyList(),
                new ArrayList<Object>() {{
                    add(q);
                    add(ImmutableContext.empty());
                    add(newName);
                }});

        BinaryOperator op = (BinaryOperator) from.getProof().getExpression();
        Quantifier quant = (Quantifier) op.right;
        Letter let = quant.letter;

        GProof to = GProofBuilder.renameForAll(Collections.emptyList(),
                new ArrayList<Object>() {{
                    add(quant);
                    add(ImmutableContext.empty());
                    add(letter.getName());
                }});

        return new PreliminaryFormStep(let, true, quant, from, to);
    }

//    @Override
//    public PreliminaryFormStep preliminaryFormStep(Renamer renamer, boolean operations) {
//        if (operations) {
//            String oldName = letter.getName();
//            String newName = renamer.getNext();
//            return new PreliminaryFormStep(new Letter(newName), true, expression.renameLetter(oldName, newName));
//        }
//        return expression.preliminaryFormStep(renamer, false);
//    }
}
