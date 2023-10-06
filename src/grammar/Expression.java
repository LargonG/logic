package grammar;

import grammar.descriptions.gilbert.GuilbertRule;
import grammar.operators.Operator;
import grammar.predicates.arithmetic.Letter;
import grammar.proof.GProof;
import grammar.proof.NProof;
import grammar.proof.builder.GProofBuilder;
import grammar.proof.context.ImmutableContext;
import util.Renamer;

import java.util.*;
import java.util.stream.Collectors;

public interface Expression {
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
        if (operator == Operator.OR) {
            return new BinaryOr(left, right);
        } else if (operator == Operator.IMPL) {
            return new BinaryImplement(left, right);
        } else if (operator == Operator.AND) {
            return new BinaryAnd(left, right);
        }
        return new BinaryOperator(operator, left, right);
    }

    static List<Expression> decomposition(Expression expression) {
        BinaryOperator op = (BinaryOperator) expression;
        return Arrays.asList(op.left, op.right);
    }

    static List<Expression> decomposition(Expression expression, Operator operator) {
        BinaryOperator op = (BinaryOperator) expression;
        if (op.operator != operator) {
            throw new IllegalStateException("Binary operator is not " + operator + " actual: " + op.operator);
        }
        return Arrays.asList(op.left, op.right);
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

    boolean calculate(Map<String, Boolean> values);

    Expression paste(Map<String, Expression> values);

    Expression toNormalForm();

    NProof createNProof(ImmutableContext context);

    /**
     * Строит доказательство данного высказывания
     *
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

            result.add(createNProof(
                    new ImmutableContext(context.entrySet().stream()
                            .map(entry -> {
                                Variable v = new Variable(entry.getKey());
                                return entry.getValue() ? v :
                                        Expression.create(Operator.IMPL, v, Nil.getInstance());
                            }).collect(Collectors.toList())
                    )));
        }
        return result;
    }

    String suffixString(Operator before, boolean brackets);

    default String suffixString() {
        return suffixString(null, false);
    }

//    default Set<String> getFreeLettersNames() {
//        Map<String, Integer> result = new HashMap<>();
//        getFreeLettersNames(result);
//        return result.entrySet().stream()
//                .filter(entry -> entry.getValue() > 0)
//                .map(Map.Entry::getKey)
//                .collect(Collectors.toSet());
//    }
//
//    void getFreeLettersNames(Map<String, Integer> result);

    default List<String> getVariablesNames() {
        Set<String> result = new HashSet<>();
        getVariablesNames(result);
        return new ArrayList<>(result);
    }

    void getVariablesNames(Set<String> result);

    default Set<String> getLettersNames() {
        Set<String> result = new HashSet<>();
        getLettersNames(result);
        return result;
    }

    void getLettersNames(Set<String> result);

    default Set<Letter> getLetters() {
        Set<Letter> letters = new HashSet<>();
        getLetters(letters);
        return letters;
    }

    void getLetters(Set<Letter> letters);

    boolean canRenameLetter(String oldName, String newName);

    Expression renameLetter(String oldName, String newName);

    PreliminaryFormStep preliminaryFormStep(Renamer renamer, boolean restruct, boolean operations);

    default PreliminaryForm toPreliminaryForm() {
        Renamer renamer = new Renamer(getLettersNames());
        GProofBuilder builder = new GProofBuilder();
        GProof from = GProofBuilder.aa(this, ImmutableContext.empty());
        GProof to = from;
        Expression current = this;
        PreliminaryFormStep step;
        while (true) {
            step = current.preliminaryFormStep(renamer, true, false);
            if (step.letter == null) {
                break;
            }
            GProof stepRight =
                    GProofBuilder.stepRight(
                            Collections.singletonList(from),
                            Collections.singletonList(step.expression));
            GProof stepLeft =
                    GProofBuilder.stepLeft(
                            Collections.singletonList(to),
                            Collections.singletonList(step.expression));
            builder
                    .append(stepRight)
                    .append(step.from)
                    .append(((BinaryOperator) stepRight.getProof().getExpression()).right,
                            ImmutableContext.empty(),
                            GuilbertRule.MODUS_PONENS, -1, -2);
            from = builder.get();
            builder
                    .append(stepLeft)
                    .append(step.to)
                    .append(((BinaryOperator) stepLeft.getProof().getExpression()).right,
                            ImmutableContext.empty(),
                            GuilbertRule.MODUS_PONENS, -1, -2);
            to = builder.get();
            current = step.expression;
        }
        return new PreliminaryForm(from, to);
    }

    class PreliminaryFormStep {
        public final Letter letter;
        public final boolean forall;
        public final Expression expression;

        public final GProof from;
        public final GProof to;

        public PreliminaryFormStep(final Letter letter,
                                   final boolean forall,
                                   final Expression expression,
                                   final GProof from,
                                   final GProof to) {
            this.letter = letter;
            this.forall = forall;
            this.expression = expression;
            this.from = from;
            this.to = to;
        }
    }

    class PreliminaryForm {
        public final GProof from;
        public final GProof to;

        public PreliminaryForm(GProof from, GProof to) {
            this.from = from;
            this.to = to;
        }
    }


}
