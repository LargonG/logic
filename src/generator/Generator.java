package generator;

import parser.*;

import java.util.Random;

public class Generator {
    private final Random random;

    public Generator(int seed) {
        this.random = new Random(seed);
    }

    public Generator() {
        this.random = new Random();
    }

    /**
     * Generate expression, which structure is: <br>
     * binOp(binOp(binOp(...), binOp()), binOp())
     * @param k -- number of binary operations (k > 0)
     * @return binOp(binOp(binOp(...), binOp()), binOp()) which deep is k
     */
    public Expression generate(int k) {
        Expression left = new BinaryOperator(chooseOperator(3), createUnary(), createUnary());
        for (int i = 0; i < k; i++) {
            Expression right = new BinaryOperator(chooseOperator(3), createUnary(), createUnary());
            left = new BinaryOperator(chooseOperator(3), left, right);
        }

        return left;
    }

    private Operator chooseOperator(int mod) {
        Operator[] operators = new Operator[] {Operator.OR, Operator.AND, Operator.IMPL, Operator.NOT};
        return operators[randint(mod)];
    }

    private Expression createUnary() {
        int varlen = randint(1) + 1;
        if (random.nextInt() < 0) {
            return new UnaryOperator(Operator.NOT, new Variable(createName(varlen)));
        } else {
            return new Variable(createName(varlen));
        }
    }

    private String createName(int len) {
        StringBuilder builder = new StringBuilder();
        String letters = "QWERTYUIOPASDFGHJKLZXCVBNM";
        String digits = "0123456789'";
        String all = letters + digits;
        builder.append(letters.charAt(randint(letters.length())));
        for (int i = 1; i < len; i++) {
            builder.append(all.charAt(randint(all.length())));
        }
        return builder.toString();
    }

    private int randint(int mod) {
        return Math.abs(random.nextInt()) % mod;
    }
}
