package grammar;

import builder.proof.Context;
import builder.proof.NProof;
import builder.proof.Proof;
import grammar.operators.Operator;

import java.util.*;
import java.util.stream.Collectors;

public interface Expression extends Comparable<Expression> {
    boolean calculate(Map<String, Boolean> values);
    Expression paste(Map<String, Expression> values);

    NProof createNProof(Context context);

    /**
     * Строит доказательство данного высказывания
     * @return доказательство без гипотез, если формула общезначима, иначе -- ничего не гарантируется
     */
    default List<NProof> createNProof() {
        List<String> vars = getVariablesNames();
        Map<String, Boolean> context = new HashMap<>();
        List<NProof> result = new ArrayList<>((1 << vars.size()));
        for (int i = 0; i < (1 << vars.size()); i++) {
            for (int j = 0; j < vars.size(); j++) {
                context.put(vars.get(j), (i & (1 << j)) == 0);
            }
            result.add(createNProof(new Context(
                    context.entrySet().stream().map(entry -> {
                        Variable v = new Variable(entry.getKey());
                        return entry.getValue() ? v : Expression.create(Operator.IMPL, v, Nil.getInstance());
                    }).collect(Collectors.toList())
            )));
        }
        return result;
    }

    String suffixString(Operator before, boolean brackets);
    default String suffixString() {
        return suffixString(null, false);
    }

    default List<String> getVariablesNames() {
        Set<String> result = new HashSet<>();
        getVariablesNames(result);
        return new ArrayList<>(result);
    }

    void getVariablesNames(Set<String> result);

    static Expression create(Operator operator, Expression left, Expression right) {
        if (operator == null) {
            assert left == null || right == null;
            return left != null ? left : right;
        }

        if (operator.unary) {
            assert left != null || right != null;
            assert left == null || right == null;
            return new UnaryOperator(operator, left != null ? left : right);
        }

        assert left != null && right != null;
        return new BinaryOperator(operator, left, right);
    }

    static List<Expression> separate(Expression expr, Operator by) {
        return separate(expr, by, true, 0, false);
    }

    static List<Expression> separate(Expression expr, Operator by, int maxDeep) {
        return separate(expr, by, true, maxDeep, true);
    }

    static List<Expression> separate(Expression expr,
                                     Operator by, boolean useOperator,
                                     int maxDeep, boolean useDeep) {
        List<Expression> result = new ArrayList<>();
        while ((!useDeep || maxDeep-- > 0) && expr instanceof BinaryOperator) {
            BinaryOperator operation = (BinaryOperator) expr;
            if (useOperator && operation.operator != by) {
                break;
            }
            result.add(operation.operator.leftAssoc ? operation.right : operation.left);
            expr = operation.operator.leftAssoc ? operation.left : operation.right;
        }
        result.add(expr);

        return result;
    }


}