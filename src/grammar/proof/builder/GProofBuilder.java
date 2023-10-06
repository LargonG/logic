package grammar.proof.builder;

import grammar.Expression;
import grammar.UnaryOperator;
import grammar.descriptions.gilbert.GuilbertDescription;
import grammar.descriptions.gilbert.GuilbertRule;
import grammar.operators.Operator;
import grammar.predicates.arithmetic.Letter;
import grammar.predicates.quantifiers.Exists;
import grammar.predicates.quantifiers.ForAll;
import grammar.predicates.quantifiers.Quantifier;
import grammar.proof.GProof;
import grammar.proof.context.ImmutableContext;
import parser.ExpressionParser;
import parser.Parser;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GProofBuilder {
    private final static Parser<Expression> EXPRESSION_PARSER = new ExpressionParser();

    List<GProof> proofs;

    public GProofBuilder() {
        proofs = new ArrayList<>();
    }

    public static GProof mergeImplicationLeft(List<GProof> proofs) {
        assert proofs.size() == 4;
        GProof ab = proofs.get(0);
        GProof ba = proofs.get(1);
        GProof cd = proofs.get(2);
        GProof dc = proofs.get(3);

        ImmutableContext context = ab.getProof().getContext();
        List<Expression> decompAb = Expression.decomposition(ab.getProof().getExpression(), Operator.IMPL);
        List<Expression> decompCd = Expression.decomposition(cd.getProof().getExpression(), Operator.IMPL);
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", decompAb.get(0));
            put("b", decompAb.get(1));
            put("c", decompCd.get(0));
            put("d", decompCd.get(1));
        }};
        Expression ac = parse("a->c", convert);
        ImmutableContext newContext = context.merge(ac);

        GProofBuilder builder = new GProofBuilder();
        builder
                .append(cd)
                .append(GProofBuilder::toContext,
                        Collections.singletonList(ac), -1)
                .append(ac, newContext, GuilbertRule.HYPOTHESIS)
                .append(GProofBuilder::transitive, -1, -2)
                .append(ba)
                .append(GProofBuilder::toContext,
                        Collections.singletonList(ac), -1)
                .append(GProofBuilder::transitive, -1, -3)
                .append("(a->c)->(b->d)", convert, context, GuilbertRule.DEDUCTION, -1)
        ;
        return builder.get().unpackDeduction();
    }

    public static GProof mergeImplicationRight(List<GProof> proofs) {
        return mergeImplicationLeft(new ArrayList<GProof>() {{
            add(proofs.get(2));
            add(proofs.get(3));
            add(proofs.get(0));
            add(proofs.get(1));
        }});
    }

    public static GProof mergeExists(List<GProof> proofs, List<Object> args) {
        assert proofs.size() == 1;
        GProof ab = proofs.get(0);
        Letter letter = (Letter) args.get(0);

        List<Expression> d = Expression.decomposition(ab.getProof().getExpression(), Operator.IMPL);
        ImmutableContext context = ab.getProof().getContext();

        ImmutableContext newContext = context.merge(d.get(1));

        Expression a = d.get(0);
        Expression ea = new Exists(letter, a);
        Expression b = d.get(1);
        Expression eb = new Exists(letter, b);
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", a);
            put("b", b);
            put("ea", ea);
            put("eb", eb);
        }};
        GProofBuilder builder = new GProofBuilder();
        builder
                .append(ab)
                .append("b->eb", convert, context, GuilbertRule.AXIOM)
                .append(GProofBuilder::transitive, -2, -1)
                .append("ea->eb", convert, context, GuilbertRule.EXISTS, -1)
        ;
        return builder.get();
    }

    public static GProof mergeForAll(List<GProof> proofs, List<Object> args) {
        assert proofs.size() == 1;
        GProof ab = proofs.get(0);
        Letter letter = (Letter) args.get(0);

        List<Expression> d = Expression.decomposition(ab.getProof().getExpression(), Operator.IMPL);
        ImmutableContext context = ab.getProof().getContext();

        Expression a = d.get(0);
        Expression fa = new ForAll(letter, a);
        Expression b = d.get(1);
        Expression fb = new ForAll(letter, b);
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", a);
            put("b", b);
            put("fa", fa);
            put("fb", fb);
        }};
        GProofBuilder builder = new GProofBuilder();
        builder
                .append(ab)
                .append("fa->a", convert, context, GuilbertRule.AXIOM)
                .append(GProofBuilder::transitive, -1, -2)
                .append("fa->fb", convert, context, GuilbertRule.FORALL, -1)
        ;
        return builder.get();
    }

    public static GProof renameForAll(List<GProof> proofs, List<Object> args) {
        assert (proofs == null || proofs.isEmpty()) && args.size() == 3;

        Quantifier quantifier = (Quantifier) args.get(0);
        ImmutableContext context = (ImmutableContext) args.get(1);
        String newName = (String) args.get(2);

        Letter let = quantifier.letter;

        Expression a = quantifier.expression;
        Expression fa = quantifier;
        Expression b = quantifier.expression.renameLetter(let.getName(), newName);
        Expression fb = new ForAll(new Letter(newName), b);

        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", a);
            put("b", b);
            put("fa", fa);
            put("fb", fb);
        }};

        GProofBuilder builder = new GProofBuilder();
        builder
                .append("fa->b", convert, context, GuilbertRule.AXIOM)
                .append("fa->fb", convert, context, GuilbertRule.FORALL, -1)
        ;
        return builder.get();
    }

    public static GProof renameExists(List<GProof> proofs, List<Object> args) {
        assert (proofs == null || proofs.isEmpty()) && args.size() == 3;

        Quantifier quantifier = (Quantifier) args.get(0);
        ImmutableContext context = (ImmutableContext) args.get(1);
        String newName = (String) args.get(2);

        Letter let = quantifier.letter;

        Expression a = quantifier.expression;
        Expression ea = quantifier;
        Expression b = quantifier.expression.renameLetter(let.getName(), newName);
        Expression eb = new Exists(new Letter(newName), b);
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", a);
            put("b", b);
            put("ea", ea);
            put("eb", eb);
        }};

        GProofBuilder builder = new GProofBuilder();
        builder
                .append("a->eb", convert, context, GuilbertRule.AXIOM)
                .append("ea->eb", convert, context, GuilbertRule.EXISTS, -1)
        ;
        return builder.get();
    }

    public static GProof stepLeft(List<GProof> proofs, List<Object> args) {
        assert proofs.size() == 1 && args.size() == 1;
        GProof ab = proofs.get(0);
        Expression p = (Expression) args.get(0);

        List<Expression> decomp = Expression.decomposition(ab.getProof().getExpression(), Operator.IMPL);
        ImmutableContext context = ab.getProof().getContext();
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", decomp.get(0));
            put("b", decomp.get(1));
            put("p", p);
        }};

        GProofBuilder builder = new GProofBuilder();
        builder
                .append(ab)
                .append("(a->b)->p->(a->b)", convert, context, GuilbertRule.AXIOM)
                .append("p->a->b", convert, context, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("(p->a->b)->(p->a)->(p->a->b)", convert, context, GuilbertRule.AXIOM)
                .append("(p->a)->(p->a->b)", convert, context, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("((p->a)->p->a->b)->((p->a)->(p->a->b)->(p->b))->((p->a)->(p->b))",
                        convert, context, GuilbertRule.AXIOM)
                .append("((p->a)->(p->a->b)->(p->b))->((p->a)->(p->b))",
                        convert, context, GuilbertRule.MODUS_PONENS,
                        -2, -1)
                .append("(p->a)->(p->a->b)->(p->b)", convert, context, GuilbertRule.AXIOM)
                .append("(p->a)->(p->b)", convert, context, GuilbertRule.MODUS_PONENS, -1, -2)
        ;
        return builder.get();
    }

    public static GProof stepRight(List<GProof> proofs, List<Object> args) {
        assert proofs.size() == 1;
        GProof ab = proofs.get(0);
        Expression p = (Expression) args.get(0);

        List<Expression> decomp = Expression.decomposition(ab.getProof().getExpression(), Operator.IMPL);
        ImmutableContext context = ab.getProof().getContext();
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", decomp.get(0));
            put("b", decomp.get(1));
            put("p", p);
        }};

        GProofBuilder builder = new GProofBuilder();
        builder
                .append(ab)
                .append("(a->b)->(a->b->p)->(a->p)", convert, context, GuilbertRule.AXIOM)
                .append("(a->b->p)->(a->p)", convert, context, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("(b->p)->(a->b->p)", convert, context, GuilbertRule.AXIOM)
                .append(GProofBuilder::transitive, -1, -2)
        ;
        return builder.get();
    }

    public static GProof implToOr(List<GProof> proofs) {
        assert proofs.size() == 1;

        GProof proof = proofs.get(0);
        Expression ab = proof.getProof().getExpression();
        ImmutableContext context = proof.getProof().getContext();

        List<Expression> d = Expression.decomposition(ab, Operator.IMPL);

        Expression a = d.get(0);
        Expression b = d.get(1);
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", a);
            put("b", b);
        }};

        GProofBuilder builder = new GProofBuilder();
        builder
                .append(proof)
                .append("b->!a|b", convert, context, GuilbertRule.AXIOM)
                .append(GProofBuilder::transitive, -2, -1)
                .append("!a->!a|b", convert, context, GuilbertRule.AXIOM)
                .append("(a->!a|b)->(!a->!a|b)->(a|!a->!a|b)", convert, context, GuilbertRule.AXIOM)
                .append("(!a->!a|b)->(a|!a->!a|b)", convert, context, GuilbertRule.MODUS_PONENS, -3, -1)
                .append("a|!a->!a|b", convert, context, GuilbertRule.MODUS_PONENS, -3, -1)
                .append(GProofBuilder::exclusiveThird,
                        new ArrayList<Object>() {{
                            add(a);
                            add(context);
                        }})
                .append("!a|b", convert, context, GuilbertRule.MODUS_PONENS, -1, -2)
        ;
        return builder.get();
    }

    public static GProof orToImpl(List<GProof> proofs) {
        assert proofs.size() == 1;

        GProof proof = proofs.get(0);
        Expression naob = proof.getProof().getExpression();
        ImmutableContext context = proof.getProof().getContext();

        List<Expression> d = Expression.decomposition(naob, Operator.OR);

        Expression na = d.get(0);
        Expression a = ((UnaryOperator) na).expr;
        Expression b = d.get(1);
        Expression nb = Expression.create(Operator.NOT, b, null);
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", a);
            put("na", na);
            put("b", b);
            put("nb", nb);
        }};

        ImmutableContext newContext = context.merge(a, na);

        GProofBuilder builder = new GProofBuilder();
        builder
                .append("(na->a->b)->(b->a->b)->(na|b->a->b)", convert, context, GuilbertRule.AXIOM)
                .append("(nb->a)->(nb->na)->!nb", convert, newContext, GuilbertRule.AXIOM)
                .append("a->nb->a", convert, newContext, GuilbertRule.AXIOM)
                .append("na->nb->na", convert, newContext, GuilbertRule.AXIOM)
                .append(a, newContext, GuilbertRule.HYPOTHESIS)
                .append("nb->a", convert, newContext, GuilbertRule.MODUS_PONENS, -1, -3)
                .append(na, newContext, GuilbertRule.HYPOTHESIS)
                .append("nb->na", convert, newContext, GuilbertRule.MODUS_PONENS, -1, -4)
                .append("(nb->na)->!nb", convert, newContext, GuilbertRule.MODUS_PONENS, -3, -7)
                .append("!nb", convert, newContext, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("!nb->b", convert, newContext, GuilbertRule.AXIOM)
                .append(b, newContext, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("na->a->b", convert, context, GuilbertRule.DEDUCTION, -1)
                .append(builder.get().unpackDeduction())
                .append("b->a->b", convert, context, GuilbertRule.AXIOM)
                .append("(b->a->b)->(na|b->a->b)", convert, context, GuilbertRule.MODUS_PONENS, -2, 0)
                .append("na|b->a->b", convert, context, GuilbertRule.MODUS_PONENS, -2, -1)
                .append(proof)
                .append("a->b", convert, context, GuilbertRule.MODUS_PONENS, -1, -2)
        ;
        return builder.get();
    }

    public static GProof notInside(List<GProof> proofs, List<Object> args) {
        assert (proofs == null || proofs.isEmpty()) && args.size() == 2;

        UnaryOperator nfa = (UnaryOperator) args.get(0);
        Quantifier fa = (Quantifier) nfa.expr;

        Expression a = fa.expression;
        Expression na = Expression.create(Operator.NOT, a, null);
        Expression ena = new Exists(fa.letter, na);
        Expression nena = Expression.create(Operator.NOT, ena, null);

        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", a);
            put("na", na);
            put("ena", ena);
            put("nena", nena);
            put("fa", fa);
            put("nfa", nfa);
        }};

        ImmutableContext context = (ImmutableContext) args.get(1);
        ImmutableContext newContext = context.merge(nena);

        GProofBuilder builder = new GProofBuilder();
        builder
                .append("na->ena", convert, context, GuilbertRule.AXIOM)
                .append(GProofBuilder::contraposition, -1)
                .append("!!a->a", convert, context, GuilbertRule.AXIOM)
                .append(GProofBuilder::transitive, -2, -1)
                .append("nena->fa", convert, context, GuilbertRule.FORALL, -1)
                .append(GProofBuilder::contraposition, -1)
                .append("!nena->ena", convert, context, GuilbertRule.AXIOM)
                .append(GProofBuilder::transitive, -2, -1)
        ;
        return builder.get();
    }

    public static GProof deductionExistHelperFirst(List<GProof> proofs, List<Object> args) {
        assert (proofs == null || proofs.isEmpty()) && args.size() == 4;

        Expression a = (Expression) args.get(0);
        Expression b = (Expression) args.get(1);
        Expression c = (Expression) args.get(2);
        ImmutableContext context = (ImmutableContext) args.get(3);
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", a);
            put("b", b);
            put("c", c);
        }};
        Expression impl = parse("a->b->c", convert);
        convert.put("impl", impl);

        ImmutableContext newContext = context.merge(impl, a, b);

        GProofBuilder builder = new GProofBuilder();
        builder
                .append(impl, newContext, GuilbertRule.HYPOTHESIS)
                .append(a, newContext, GuilbertRule.HYPOTHESIS)
                .append(b, newContext, GuilbertRule.HYPOTHESIS)
                .append("b->c", convert, newContext, GuilbertRule.MODUS_PONENS, -2, -3)
                .append(c, newContext, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("impl->b->a->c", convert, context, GuilbertRule.DEDUCTION, -1)
                .append(builder.get().unpackDeduction())
        ;
        return builder.get();
    }

    public static GProof deductionExistHelperSecond(List<GProof> proofs, List<Object> args) {
        return deductionExistHelperFirst(proofs, new ArrayList<Object>() {{
            add(args.get(1));
            add(args.get(0));
            add(args.get(2));
            add(args.get(3));
        }});
    }

    public static GProof deductionForAllHelperFirst(List<GProof> proofs, List<Object> args) {
        assert (proofs == null || proofs.isEmpty()) && args.size() == 4;

        Expression a = (Expression) args.get(0);
        Expression b = (Expression) args.get(1);
        Expression c = (Expression) args.get(2);
        ImmutableContext context = (ImmutableContext) args.get(3);
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", a);
            put("b", b);
            put("c", c);
        }};
        Expression impl = parse("a->b->c", convert);
        convert.put("impl", impl);
        Expression aAb = parse("a&b", convert);
        convert.put("aAb", aAb);

        ImmutableContext newContext = context.merge(impl, aAb);

        GProofBuilder builder = new GProofBuilder();
        builder
                .append(aAb, newContext, GuilbertRule.HYPOTHESIS)
                .append("aAb->a", convert, newContext, GuilbertRule.AXIOM)
                .append(a, newContext, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("aAb->b", convert, newContext, GuilbertRule.AXIOM)
                .append(b, newContext, GuilbertRule.MODUS_PONENS, -4, -1)
                .append(impl, newContext, GuilbertRule.HYPOTHESIS)
                .append("b->c", convert, newContext, GuilbertRule.MODUS_PONENS, -4, -1)
                .append(c, newContext, GuilbertRule.MODUS_PONENS, -3, -1)
                .append("impl->aAb->c", convert, context, GuilbertRule.DEDUCTION, -1)
                .append(builder.get().unpackDeduction())
        ;
        return builder.get();
    }

    public static GProof deductionForAllHelperSecond(List<GProof> proofs, List<Object> args) {
        assert (proofs == null || proofs.isEmpty()) && args.size() == 4;

        Expression a = (Expression) args.get(0);
        Expression b = (Expression) args.get(1);
        Expression c = (Expression) args.get(2);
        ImmutableContext context = (ImmutableContext) args.get(3);
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", a);
            put("b", b);
            put("c", c);
        }};
        Expression impl = parse("a->b->c", convert);
        convert.put("impl", impl);
        Expression aAb = parse("a&b", convert);
        convert.put("aAb", aAb);
        Expression aAbIc = parse("aAb->c", convert);
        convert.put("aAbIc", aAbIc);

        ImmutableContext newContext = context.merge(aAbIc, a, b);

        GProofBuilder builder = new GProofBuilder();
        builder
                .append("a->b->aAb", convert, newContext, GuilbertRule.AXIOM)
                .append(a, newContext, GuilbertRule.HYPOTHESIS)
                .append("b->aAb", convert, newContext, GuilbertRule.MODUS_PONENS, -1, -2)
                .append(b, newContext, GuilbertRule.HYPOTHESIS)
                .append(aAb, newContext, GuilbertRule.MODUS_PONENS, -1, -2)
                .append(aAbIc, newContext, GuilbertRule.AXIOM)
                .append(c, newContext, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("aAbIc->impl", convert, context, GuilbertRule.DEDUCTION, -1)
                .append(builder.get().unpackDeduction())
        ;
        return builder.get();
    }

    public static GProof exclusiveThird(List<GProof> proofs, List<Object> args) {
        assert proofs.isEmpty() && args.size() == 2;
        Expression expression = (Expression) args.get(0);
        ImmutableContext context = (ImmutableContext) args.get(1);
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", expression);
        }};
        GProofBuilder builder = new GProofBuilder();
        builder
                .append(
                        "(!(a|!a)->a)->(!(a|!a)->!a)->!!(a|!a)",
                        convert,
                        context,
                        GuilbertRule.AXIOM)
                .append("!a->a|!a", convert, context, GuilbertRule.AXIOM)
                .append(GProofBuilder::contraposition, -1)
                .append("!!a->a", convert, context, GuilbertRule.AXIOM)
                .append(GProofBuilder::transitive, -2, -1)
                .append("(!(a|!a)->!a)->!!(a|!a)", convert, context, GuilbertRule.MODUS_PONENS, -1, -5)
                .append("a->a|!a", convert, context, GuilbertRule.AXIOM)
                .append(GProofBuilder::contraposition, -1)
                .append("!!(a|!a)", convert, context, GuilbertRule.MODUS_PONENS, -1, -3)
                .append("!!(a|!a)->(a|!a)", convert, context, GuilbertRule.AXIOM)
                .append("a|!a", convert, context, GuilbertRule.MODUS_PONENS, -2, -1);
        return builder.get();
    }

    public static GProof contraposition(List<GProof> proofs) {
        assert proofs.size() == 1;
        GProof proof = proofs.get(0);

        GProofBuilder builder = new GProofBuilder();

        List<Expression> decomp = Expression.decomposition(proof.getProof().getExpression(), Operator.IMPL);
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", decomp.get(0));
            put("b", decomp.get(1));
        }};
        Expression notB = parse("!b", convert);

        ImmutableContext newContext = proof.getProof().getContext().merge(
                proof.getProof().getExpression(),
                notB
        );

        builder
                .append("(a->b)->(a->!b)->!a", convert, newContext, GuilbertRule.AXIOM)
                .append("a->b", convert, newContext, GuilbertRule.HYPOTHESIS)
                .append("(a->!b)->!a", convert, newContext, GuilbertRule.MODUS_PONENS, -1, -2)
                .append("!b->a->!b", convert, newContext, GuilbertRule.AXIOM)
                .append("!b", convert, newContext, GuilbertRule.HYPOTHESIS)
                .append("a->!b", convert, newContext, GuilbertRule.MODUS_PONENS, -1, -2)
                .append("!b", convert, newContext, GuilbertRule.MODUS_PONENS, -1, -4)
                .append("(a->b)->!b->!a", convert, proof.getProof().getContext(),
                        GuilbertRule.DEDUCTION, -1)
                .append(builder.get().unpackDeduction())
                .append(proof)
                .append("!b->!a", convert, proof.getProof().getContext(), GuilbertRule.MODUS_PONENS, -1, -2);
        return builder.get();
    }

    public static GProof transitive(List<GProof> proofs) {
        assert proofs.size() == 2;
        GProof from = proofs.get(0);
        GProof to = proofs.get(1);

        List<Expression> decompFrom = Expression.decomposition(from.getProof().getExpression(), Operator.IMPL);
        List<Expression> decompTo = Expression.decomposition(to.getProof().getExpression(), Operator.IMPL);

        ImmutableContext context = from.getProof().getContext();
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", decompFrom.get(0));
            put("b", decompFrom.get(1));
            put("c", decompTo.get(1));
        }};

        GProofBuilder builder = new GProofBuilder();
        builder
                .append(from)
                .append("(a->b)->(a->b->c)->(a->c)", convert, context, GuilbertRule.AXIOM)
                .append("(a->b->c)->(a->c)", convert, context, GuilbertRule.MODUS_PONENS, -2, -1)
                .append("(b->c)->a->(b->c)", convert, context, GuilbertRule.AXIOM)
                .append(to)
                .append("a->b->c", convert, context, GuilbertRule.MODUS_PONENS, -1, -2)
                .append("a->c", convert, context, GuilbertRule.MODUS_PONENS, -1, -4)
        ;
        return builder.get();
    }

    public static GProof toContext(List<GProof> proofs, List<Object> args) {
        assert proofs.size() == 2;
        GProof isTrue = proofs.get(0);
        Expression toContext = (Expression) args.get(0);

        ImmutableContext context = isTrue.getProof().getContext();
        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", isTrue.getProof().getExpression());
            put("b", toContext);
        }};

        GProofBuilder builder = new GProofBuilder();
        builder
                .append("a->b->a", convert, context, GuilbertRule.AXIOM)
                .append(isTrue)
                .append("b->a", convert, context, GuilbertRule.MODUS_PONENS, -1, -2)
                .append("a", convert, context.merge(toContext),
                        GuilbertRule.DEDUCTION, -1)
        ;
        return builder.get().unpackDeduction();
    }

    public static GProof aa(List<GProof> proofs, List<Object> args) {
        assert (proofs == null || proofs.isEmpty()) && args.size() == 2;

        Expression expr = (Expression) args.get(0);
        ImmutableContext context = (ImmutableContext) args.get(1);

        Map<String, Expression> convert = new HashMap<String, Expression>() {{
            put("a", expr);
        }};

        GProofBuilder builder = new GProofBuilder();
        builder
                .append(expr, context.merge(expr), GuilbertRule.HYPOTHESIS)
                .append("a->a", convert, context, GuilbertRule.DEDUCTION, -1)
                .append(builder.get().unpackDeduction());
        return builder.get();
    }

    public static GProof aa(Expression self, ImmutableContext context) {
        return GProofBuilder.aa(null, new ArrayList<Object>() {{
            add(self);
            add(context);
        }});
    }

    public static GProof deductionLeft(List<GProof> proofs) {
        assert proofs.size() == 1;

        GProof proof = proofs.get(0);
        List<Expression> d = Expression.decomposition(proof.getProof().getExpression(), Operator.IMPL);
        ImmutableContext newContext = proof.getProof().getContext().merge(d.get(0));
        GProofBuilder builder = new GProofBuilder();
        builder
                .append(proof)
                .append(d.get(1), newContext, GuilbertRule.DEDUCTION, -1)
                .append(builder.get().unpackDeduction());
        return builder.get();
    }

    private static Expression parse(String input, Map<String, Expression> pasting) {
        return EXPRESSION_PARSER.parse(input).paste(pasting);
    }

    public GProofBuilder append(final GProof gProof) {
        proofs.add(gProof);
        return this;
    }

    public GProofBuilder append(final Expression expression,
                                final ImmutableContext context,
                                final GuilbertRule rule,
                                int... ids) {
        proofs.add(new GProof(expression, context, new GuilbertDescription(rule, toGProofs(ids))));
        return this;
    }

    public GProofBuilder append(final String expression,
                                final Map<String, Expression> pasting,
                                final ImmutableContext context,
                                final GuilbertRule rule,
                                int... ids) {
        proofs.add(
                new GProof(parse(expression, pasting),
                        context,
                        new GuilbertDescription(rule, toGProofs(ids))));
        return this;
    }

    public GProofBuilder append(final Function<List<GProof>, GProof> func,
                                final int... ids) {
        return append(func.apply(toGProofs(ids)));
    }

    public GProofBuilder append(final BiFunction<List<GProof>, List<Object>, GProof> func,
                                List<Object> args,
                                final int... ids) {
        return append(func.apply(toGProofs(ids), args));
    }

    private List<GProof> toGProofs(int... ids) {
        return Arrays.stream(ids).boxed()
                .map(i -> i >= 0 ? proofs.get(i) : proofs.get(proofs.size() + i))
                .collect(Collectors.toList());
    }

    public GProof get() {
        return proofs.get(proofs.size() - 1);
    }

    public GProof get(int id) {
        return proofs.get(proofs.size() + id);
    }
}
