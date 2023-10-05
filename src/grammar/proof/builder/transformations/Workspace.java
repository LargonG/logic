package grammar.proof.builder.transformations;

import grammar.Expression;
import grammar.operators.Operator;
import grammar.predicates.quantifiers.Quantifier;
import grammar.proof.GProof;
import grammar.proof.builder.GProofBuilder;
import grammar.proof.context.ImmutableContext;
import parser.ExpressionParser;
import parser.Parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Workspace {
    private final static Parser<Expression> PARSER = new ExpressionParser();
    public static Expression parse(String input, Map<String, Expression> pasting) {
        return PARSER.parse(input).paste(pasting);
    }

    public final GProof proof;
    public final ImmutableContext context;

    public final Expression left;
    public final Expression right;
    public final Expression self;

    public final Expression impl;

    public final Quantifier quantifier;

    public final Map<String, Expression> convert;

    public final GProofBuilder builder;

    Workspace(final GProof proof, boolean forall, boolean getIn, boolean checkLeft) {
        this.proof = proof;
        this.context = proof.getProof().getContext();
        this.self = proof.getProof().getExpression();


        Quantifier quant = null;
        Expression expr = self;
        // внутрь вносим квантор, значит this.self == Quantifier
        if (getIn) {
            quant = (Quantifier) this.self;
            expr = quant.expression;
        }

        List<Expression> d = Expression.decomposition(expr, Operator.IMPL);
        this.left = d.get(0);
        this.right = d.get(1);

        if (!getIn) {
            quant = (Quantifier) (checkLeft ? left : right);
        }

        this.convert = new HashMap<>();
        if (!getIn) {
            // (@x.a)->b : a->?x.b
            if (forall) {
                convert.put(checkLeft ? "ea" : "a", left);
                convert.put(checkLeft ? "b" : "fb", right);
                convert.put(checkLeft ? "a" : "b", quant.expression);
            } else {
                convert.put(checkLeft ? "fa" : "a", left);
                convert.put(checkLeft ? "b" : "eb", right);
                convert.put(checkLeft ? "a" : "b", quant.expression);
            }

        } else {
            // @x.a->b | ?x.a->b
            convert.put("a", left);
            convert.put("b", right);
        }
        impl = parse("a->b", convert);

        convert.put("self", self);
        convert.put("impl", impl);

        this.quantifier = quant;
        this.builder = new GProofBuilder();
    }
}
