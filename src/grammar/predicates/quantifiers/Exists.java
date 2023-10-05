package grammar.predicates.quantifiers;

import grammar.BinaryOperator;
import grammar.Expression;
import grammar.predicates.arithmetic.Letter;
import grammar.proof.GProof;
import grammar.proof.builder.GProofBuilder;
import grammar.proof.context.ImmutableContext;
import util.Renamer;

import java.util.*;

public class Exists extends Quantifier {
    public Exists(Letter letter, Expression expression) {
        super(letter, expression, "?");
    }

    @Override
    public Expression paste(Map<String, Expression> values) {
        Expression newExpr = expression.paste(values);
        if (newExpr != expression) {
            return new Exists(letter, newExpr);
        }
        return this;
    }

    @Override
    public Expression toNormalForm() {
        Expression newExpr = expression.toNormalForm();
        if (newExpr != expression) {
            return new Exists(letter, newExpr);
        }
        return this;
    }

    @Override
    public Expression renameLetter(String oldName, String newName) {
        if (!letter.getName().equals(oldName)) {
            Expression newExpr = expression.renameLetter(oldName, newName);
            if (newExpr != expression) {
                return new Exists(letter, newExpr);
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
            GProof from = GProofBuilder.mergeExists(Collections.singletonList(step.from), Collections.singletonList(letter));
            GProof to = GProofBuilder.mergeExists(Collections.singletonList(step.to), Collections.singletonList(letter));
            return new PreliminaryFormStep(null, true,
                    step.expression == expression ? this : new Exists(letter, step.expression), from, to);
        }

        // Иначе мы пришли к изменению

        String newName = renamer.getNext();
        GProof from = GProofBuilder.renameExists(Collections.emptyList(),
                new ArrayList<Object>() {{
                    add(this);
                    add(ImmutableContext.empty());
                    add(newName);
                }});

        BinaryOperator op = (BinaryOperator) from.getProof().getExpression();
        Quantifier quant = (Quantifier) op.right;
        Letter let = quant.letter;

        GProof to = GProofBuilder.renameExists(Collections.emptyList(),
                new ArrayList<Object>() {{
                    add(quant);
                    add(ImmutableContext.empty());
                    add(letter.getName());
                }});

        return new PreliminaryFormStep(let, false, quant, from, to);
    }
}
