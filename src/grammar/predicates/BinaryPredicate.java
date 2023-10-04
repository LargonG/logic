package grammar.predicates;

import grammar.Expression;
import grammar.operators.Operator;
import grammar.predicates.arithmetic.Arithmetic;
import grammar.predicates.arithmetic.Letter;

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
    public String suffixString(Operator before, boolean brackets) {
        return left.suffixString() + operator.toString() + right.suffixString();
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
    public Expression renameLetter(String oldName, String newName) {
        Arithmetic newLeft = left.rename(oldName, newName);
        Arithmetic newRight = right.rename(oldName, newName);

        if (newLeft != left || newRight != right) {
            return new BinaryPredicate(operator, newLeft, newRight);
        }
        return this;
    }
}
