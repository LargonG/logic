package builder;

import builder.descriptions.*;
import parser.*;
import resolver.Axioms;

import java.util.*;
import java.util.function.BiFunction;

public class MetaBuilder {
    private static final List<Integer> emptyList = new ArrayList<>();
    private final Parser parser;

    public MetaBuilder() {
        this.parser = new Parser();
    }

    public List<Proof> build(List<String> lines) {
        List<Proof> proofs = new ArrayList<>(lines.size());
        Map<Expression, List<Integer>> rightPartOfImplication = new TreeMap<>();
        Map<Expression, List<Integer>> expressions = new TreeMap<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] ln = line.split("[|]-");
            String[] unparsedHypotheses = ln[0].split(",");

            Map<Expression, Integer> hypotheses = new TreeMap<>();
            List<Expression> hypothesesList = new ArrayList<>(unparsedHypotheses.length);

            for (String unparsedHypothesis : unparsedHypotheses) {
                Expression hyp = parser.parse(unparsedHypothesis);
                if (hyp == null) {
                    continue;
                }
                hypotheses.merge(hyp, 1, Integer::sum);
                hypothesesList.add(hyp);
            }
            Expression expr = parser.parse(ln[1]);

            Description comment = new Incorrect();

            // Axiom
            int axiomId;
            if ((axiomId = Axioms.isAxiom(expr)) != -1) {
                comment = new AxiomScheme(axiomId);
            }

            // Hypothesis
            int hypothesisId = -1;
            if (axiomId == -1 && (hypothesisId = hypothesesList.indexOf(expr)) != -1) {
                comment = new Hypothesis(hypothesisId);
            }

            // Modus Ponens
            int modus = -1, ponens = -1;
            if (axiomId == -1 && hypothesisId == -1) {
                for (int implI : rightPartOfImplication.getOrDefault(expr, emptyList)) {
                    Proof implicationProof = proofs.get(implI);
                    BinaryOperator impl = (BinaryOperator) implicationProof.expression;
                    if (implicationProof.context.equals(hypotheses)) {
                        for (int ai : expressions.getOrDefault(impl.left, emptyList)) {
                            Proof alphaProof = proofs.get(ai);
                            if (alphaProof.context.equals(hypotheses)) {
                                modus = ai;
                                ponens = implI;
                                comment = new ModusPonens(modus, ponens);
                            }
                        }
                    }
                }
            }

            // Deduction
            Expression dedExpr = expr;
            Map<Expression, Integer> dedHypotheses = new HashMap<>();
            while (dedExpr instanceof BinaryOperator) {
                BinaryOperator impl = (BinaryOperator) dedExpr;
                if (!impl.operator.equals(Operator.IMPL)) {
                    break;
                }
                dedExpr = impl.right;
                dedHypotheses.merge(impl.left, 1, Integer::sum);
            }
            for (Map.Entry<Expression, Integer> hyp: hypotheses.entrySet()) {
                dedHypotheses.merge(hyp.getKey(), hyp.getValue(), Integer::sum);
            }

            int dedId = -1;
            if (axiomId == -1 && hypothesisId == -1 && modus == -1 && ponens == -1) {
                for (int proofId = 0; proofId < proofs.size(); proofId++) {
                    Proof proof = proofs.get(proofId);
                    if (proof.deductionExpression.equals(dedExpr) && proof.deductionContext.equals(dedHypotheses)) {
                        dedId = proofId;
                        comment = new Deduction(dedId);
                        break;
                    }
                }
            }

            // adding new params

            // proof
            int id = i;
            BiFunction<List<Integer>, List<Integer>, List<Integer>>
                    mergeLists = (oldL, newL) -> {oldL.addAll(newL); return oldL;};


            proofs.add(new Proof(expr, hypothesesList, hypotheses, dedExpr, dedHypotheses, i, comment, line));

            if (expr instanceof BinaryOperator) {
                BinaryOperator impl = (BinaryOperator) expr;
                if (impl.operator.equals(Operator.IMPL)) {
                    rightPartOfImplication.merge(impl.right, new ArrayList<Integer>() {{add(id);}}, mergeLists);
                }
            }
            expressions.merge(expr, new ArrayList<Integer>() {{add(id);}}, mergeLists);
        }

        return proofs;
    }
}
