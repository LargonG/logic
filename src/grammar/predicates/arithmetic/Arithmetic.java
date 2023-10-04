package grammar.predicates.arithmetic;

import java.util.Set;

public interface Arithmetic {
    Arithmetic rename(String oldName, String newName);

    void getLettersNames(Set<String> result);
    void getLetters(Set<Letter> letters);

    String suffixString();
    String suffixString(ArithmeticOperator before, boolean brackets);


    static Arithmetic create(final ArithmeticOperator operator,
                      final Arithmetic left,
                      final Arithmetic right) {
        if (operator == null) {
            assert left == null || right == null;
            return left != null ? left : right;
        }

        assert left != null && right != null;
        return new BinaryArithmetic(operator, left, right);
    }
}
