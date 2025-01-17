package grammar.predicates.arithmetic;

import java.util.Set;

public class BinaryArithmetic implements Arithmetic {
    private final Arithmetic left;
    private final Arithmetic right;
    private final ArithmeticOperator operator;

    public BinaryArithmetic(final ArithmeticOperator operator,
                            final Arithmetic left,
                            final Arithmetic right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public Arithmetic rename(String oldName, String newName) {
        Arithmetic newLeft = left.rename(oldName, newName);
        Arithmetic newRight = right.rename(oldName, newName);

        if (newLeft != left || newRight != right) {
            return new BinaryArithmetic(operator, newLeft, newRight);
        }
        return this;
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
    public void suffixString(StringBuilder builder, ArithmeticOperator before, boolean brackets) {
        if (before != null && (before.priority > operator.priority || before == operator && brackets)) {
            builder.append("(");
            left.suffixString(builder, operator, false);
            builder.append(operator);
            right.suffixString(builder, operator, true);
            builder.append(")");
            return;
        }
        left.suffixString(builder, operator, false);
        builder.append(operator);
        right.suffixString(builder, operator, true);
    }

    @Override
    public String toString() {
        return "(" + operator.toString() + "," + left.toString() + "," + right.toString() + ")";
    }
}
