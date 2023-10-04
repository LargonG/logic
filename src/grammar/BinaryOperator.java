package grammar;

import grammar.operators.Operator;
import grammar.predicates.arithmetic.Letter;
import grammar.proof.NProof;
import grammar.proof.Proof;
import grammar.proof.context.ImmutableContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

public class BinaryOperator implements Expression {
    public final Operator operator;
    public final Expression left;
    public final Expression right;

    private final static Map<Operator, BiFunction<Boolean, Boolean, Boolean>> mapping =
            new HashMap<Operator, BiFunction<Boolean, Boolean, Boolean>>()
            {{
                put(Operator.OR, (left, right) -> left || right);
                put(Operator.AND, (left, right) -> left && right);
                put(Operator.IMPL, (left, right) -> !left || right);
            }};

    public BinaryOperator(Operator operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + operator.toString() + "," + left.toString() + "," + right.toString() + ")";
    }

    @Override
    public boolean calculate(Map<String, Boolean> values) {
        return mapping.get(operator).apply(left.calculate(values), right.calculate(values));
    }

    @Override
    public String suffixString(Operator before, boolean brackets) {
        if (before != null && (before.priority > operator.priority || before == operator && brackets)) {
            return "(" + left.suffixString(operator, !operator.leftAssoc)
                    + operator
                    + right.suffixString(operator, operator.leftAssoc) + ")";
        }
        return left.suffixString(operator, !operator.leftAssoc)
                + operator
                + right.suffixString(operator, operator.leftAssoc);
    }

    @Override
    public void getVariablesNames(Set<String> result) {
        left.getVariablesNames(result);
        right.getVariablesNames(result);
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
        Expression newLeft = left.renameLetter(oldName, newName);
        Expression newRight = right.renameLetter(oldName, newName);

        if (newLeft != left || newRight != right) {
            return new BinaryOperator(operator, newLeft, newRight);
        }
        return this;
    }

    @Override
    public Expression paste(Map<String, Expression> values) {
        return new BinaryOperator(operator, left.paste(values), right.paste(values));
    }

    @Override
    public Expression toNormalForm() {
        Expression newLeft = left.toNormalForm();
        Expression newRight = right.toNormalForm();
        if (newLeft != left || newRight != right) {
            return new BinaryOperator(operator, newLeft, newRight);
        }
        return this;
    }

    @Override
    public NProof createNProof(ImmutableContext context) {
        NProof left = this.left.createNProof(context);
        NProof right = this.right.createNProof(context);
        return operator.createNProof(left, right, new Proof(this, context));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryOperator that = (BinaryOperator) o;
        return operator == that.operator && Objects.equals(left, that.left) && Objects.equals(right, that.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator, left, right);
    }
}
