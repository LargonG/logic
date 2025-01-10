package grammar.predicates;

import grammar.Expression;
import grammar.operators.Operator;
import grammar.predicates.arithmetic.Arithmetic;
import grammar.predicates.arithmetic.Letter;
import proof.GProof;
import proof.builder.GProofBuilder;
import proof.context.ImmutableContext;
import util.Renamer;

import java.util.Set;

public class BinaryPredicate implements Predicate {
    protected final PredicateOperator operator;
    protected final Arithmetic left;
    protected final Arithmetic right;

    public BinaryPredicate(final PredicateOperator operator,
                           final Arithmetic left,
                           final Arithmetic right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void suffixString(StringBuilder builder, Operator before, boolean brackets) {
        left.suffixString(builder, null, false);
        builder.append(operator);
        right.suffixString(builder, null, false);
    }

    @Override
    public String toString() {
        return "(" + operator.toString() + "," + left.toString() + "," + right.toString() + ")";
    }

    @Override
    public void getLettersNames(Set<String> result) {
        left.getLettersNames(result);
        right.getLettersNames(result);
    }

    @Override
    public void getLetters(Set<Letter> letters) {
        left.getLetters(letters);
        right.getLetters(letters);
    }

    @Override
    public boolean canRenameLetter(String oldName, String newName) {
        return true;
    }

    @Override
    public Expression renameLetter(String oldName, String newName) {
        Arithmetic newLeft = left.rename(oldName, newName);
        Arithmetic newRight = right.rename(oldName, newName);

        if (newLeft != left || newRight != right) {
            return new BinaryPredicate(operator, newLeft, newRight);
        }
        return this;
    }

    @Override
    public PreliminaryFormStep preliminaryFormStep(Renamer renamer, boolean restruct, boolean operations) {
        // Строим доказательство a -> a
        GProof res = GProofBuilder.aa(this, ImmutableContext.empty());
        return new PreliminaryFormStep(null, false, this, res, res);
    }
}
