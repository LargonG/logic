package grammar;

import grammar.descriptions.gilbert.GuilbertRule;
import grammar.operators.Operator;
import grammar.predicates.BinaryPredicate;
import grammar.predicates.arithmetic.Letter;
import grammar.predicates.quantifiers.Exists;
import grammar.predicates.quantifiers.ForAll;
import grammar.predicates.quantifiers.Quantifier;
import grammar.proof.GProof;
import grammar.proof.NProof;
import grammar.proof.Proof;
import grammar.proof.builder.GProofBuilder;
import grammar.proof.builder.transformations.ExistsTransformations;
import grammar.proof.builder.transformations.ForAllTransformations;
import grammar.proof.builder.transformations.Workspace;
import grammar.proof.context.Context;
import grammar.proof.context.ImmutableContext;
import util.Renamer;

import java.util.*;
import java.util.function.BiFunction;

import static sun.text.normalizer.UTF16.append;

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
    public boolean canRenameLetter(String oldName, String newName) {
        boolean leftR = left.canRenameLetter(oldName, newName);
        boolean rightR = right.canRenameLetter(oldName, newName);

        return leftR && rightR;
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
    public PreliminaryFormStep preliminaryFormStep(final Renamer renamer,
                                                   final boolean restruct,
                                                   final boolean operations) {
        if (!restruct) {
            // Строим доказательство a -> a
            GProof res = GProofBuilder.aa(this, ImmutableContext.empty());
            return new PreliminaryFormStep(null, false, this, res, res);
        }

        PreliminaryFormStep l = left.preliminaryFormStep(renamer, true, true);
        PreliminaryFormStep r = right.preliminaryFormStep(renamer, l.letter == null, true);



        if (l.letter == null && r.letter == null) {
            // Не выносим никакого квантора, значит возвращаем наше же поддерево
            GProof res = GProofBuilder.aa(this, ImmutableContext.empty());
            return new PreliminaryFormStep(null, false, this, res, res);
        }

        Expression ab = l.from.getProof().getExpression();
        Expression ba = l.to.getProof().getExpression();
        Expression cd = r.from.getProof().getExpression();
        Expression dc = r.to.getProof().getExpression();

        List<Expression> dl = Expression.decomposition(ab, Operator.IMPL);
        List<Expression> dr = Expression.decomposition(cd, Operator.IMPL);

        Expression a = dl.get(0);
        Expression b = dl.get(1);
        Expression c = dr.get(0);
        Expression d = dr.get(1);


        GProofBuilder builder = new GProofBuilder();

        builder
                .append(l.from)
                .append(l.to)
                .append(r.from)
                .append(r.to)
                .append(GProofBuilder::mergeImplicationLeft, -4, -3, -2, -1)
                .append(GProofBuilder::mergeImplicationRight, -5, -4, -3, -2);

        BinaryOperator acbd = (BinaryOperator) builder.get(-2).getProof().getExpression();
        Expression ac = acbd.left;
        Expression bd = acbd.right;

        ImmutableContext newContext1 = ImmutableContext.of(bd);
        builder
                .append(bd, newContext1, GuilbertRule.HYPOTHESIS);

        // Что-то поменялось
        if (l.letter != null) {
            // Поменяли левое поддерево, хотим поменять всё остальное
            if (l.forall) {
                // left exists !!!
                builder
                        .append(ExistsTransformations::outLeft, -1);
            } else {
                // left all
                builder
                        .append(ForAllTransformations::outLeft, -1);
                ;
            }
        } else {
            if (r.forall) {
                // right all
                builder
                        .append(ForAllTransformations::outRight, -1);
            } else {
                // right exist
                builder
                        .append(ExistsTransformations::outRight, -1);
            }
        }

        Quantifier qbd = (Quantifier) builder.get().getProof().getExpression();

        builder
                .append(Expression.create(
                        Operator.IMPL, bd, builder.get().getProof().getExpression()),
                ImmutableContext.empty(), GuilbertRule.DEDUCTION, -1)
                .append(builder.get().unpackDeduction())
                .append(GProofBuilder::transitive, 4, -1)
        ;

        GProof from = builder.get();

        ImmutableContext newContext2 = ImmutableContext.of(qbd);
        builder
                .append(qbd, newContext2, GuilbertRule.HYPOTHESIS);

        if (l.letter != null) {
            if (l.forall) {
                builder.append(ExistsTransformations::inLeft, -1);
            } else {
                builder.append(ForAllTransformations::inLeft, -1);
            }
        } else {
            if (r.forall) {
                builder.append(ForAllTransformations::inRight, -1);
            } else {
                builder.append(ExistsTransformations::inRight, -1);
            }
        }

        builder
                .append(Expression.create(
                        Operator.IMPL, qbd, bd),
                        ImmutableContext.empty(), GuilbertRule.DEDUCTION, -1)
                .append(builder.get().unpackDeduction())
                .append(GProofBuilder::transitive, 5, -1);

        GProof to = builder.get();

        return new PreliminaryFormStep(l.letter != null ? l.letter : r.letter,
                qbd instanceof ForAll, qbd, from, to);
    }

    //    @Override
//    public PreliminaryFormStep preliminaryFormStep(Renamer renamer, boolean operations) {
//        PreliminaryFormStep l = left.preliminaryFormStep(renamer, true);
//
//        BinaryOperator newOp = this;
//        if (!left.equals(l.expression)) {
//            newOp = new BinaryOperator(operator, l.expression, right);
//        }
//
//        if (l.letter != null) {
//            return new PreliminaryFormStep(null, false,
//                    l.forall ? new ForAll(l.letter, newOp) : new Exists(l.letter, newOp));
//        }
//        PreliminaryFormStep r = right.preliminaryFormStep(renamer, true);
//
//        if (!right.equals(r.expression)) {
//            newOp = new BinaryOperator(operator, left, r.expression);
//        }
//        if (r.letter != null) {
//            return new PreliminaryFormStep(null, false,
//                    r.forall ? new ForAll(r.letter, newOp) : new Exists(r.letter, newOp));
//        }
//        return new PreliminaryFormStep(null, false, newOp);
//    }

//    @Override
//    public PreliminaryForm preliminaryFormRecursion(Expression other,
//                                                    String oldName, String newName) {
//        GProofBuilder builder = new GProofBuilder();
//        if (this.equals(other)) {
//            builder
//                    .append(this, ImmutableContext.of(this), GuilbertRule.HYPOTHESIS)
//                    .append(Expression.create(Operator.IMPL, this, this),
//                            ImmutableContext.empty(), GuilbertRule.DEDUCTION, -1)
//                    .append(builder.get().unpackDeduction())
//            ;
//            return new PreliminaryForm(builder.get(), builder.get()); // this -> this
//        }
//        Quantifier quantifier = null;
//        BinaryOperator expr;
//        if (other instanceof Quantifier) {
//            // раньше его тут не было
//            quantifier = (Quantifier) other;
//            expr = (BinaryOperator) quantifier.expression;
//        } else {
//            expr = (BinaryOperator) other;
//        }
//
//        PreliminaryForm l = left.preliminaryFormRecursion(expr.left, oldName, newName);
//        PreliminaryForm r = right.preliminaryFormRecursion(expr.right, oldName, newName);
//
//        List<Expression> dlf = Expression.decomposition(l.from.getProof().getExpression(), Operator.IMPL);
//        List<Expression> drf = Expression.decomposition(r.from.getProof().getExpression(), Operator.IMPL);
//
//        Map<String, Expression> convert = new HashMap<String, Expression>() {{
//            put("a", dlf.get(0));
//            put("b", dlf.get(1));
//            put("c", drf.get(0));
//            put("d", drf.get(1));
//        }};
//
//        Expression ac = Expression.create(Operator.IMPL, dlf.get(0), drf.get(0));
//        Expression bd = Expression.create(Operator.IMPL, dlf.get(1), drf.get(1));
//
//        builder
//                .append(l.from)
//                .append(l.to)
//                .append(r.from)
//                .append(r.to)
//                .append(GProofBuilder::mergeImplicationLeft, -4, -3, -2, -1)
//                .append(GProofBuilder::mergeImplicationRight, -5, -4, -3, -2);
//
//        ImmutableContext cac = ImmutableContext.of(ac);
//        ImmutableContext cbd = ImmutableContext.of(bd);
//
//        if (other instanceof Quantifier) {
//            builder
//                    .append(ac, cac, GuilbertRule.DEDUCTION, -2)
//                    .append(builder.get().unpackDeduction())
//                    .append(bd, cbd, GuilbertRule.DEDUCTION, -3)
//                    .append(builder.get().unpackDeduction());
//            if (other instanceof Exists) {
//
//            } else {
//
//            }
//        }
//        return new PreliminaryForm(builder.get(-1), builder.get(-2));
//    }

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
