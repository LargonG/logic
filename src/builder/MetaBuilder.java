package builder;

import parser.*;
import resolver.Axioms;

import java.util.*;
import java.util.function.BiFunction;

public class MetaBuilder {
    private static final List<Integer> emptyList = new ArrayList<>();
    private static class Proof {
        Map<Expression, Integer> dedHyps;
        Map<Expression, Integer> hyps;

        Expression dedExpr;
        Expression expr;
        int id;

        public Proof(Map<Expression, Integer> dedHyps, Expression dedExpr,
                     Map<Expression, Integer> hyps, Expression expr,
                     int id) {
            this.dedHyps = dedHyps;
            this.dedExpr = dedExpr;
            this.hyps = hyps;
            this.expr = expr;
            this.id = id;
        }

        @Override
        public String toString() {
            return "Proof{" +
                    "dedHyps=" + dedHyps +
                    ", hyps=" + hyps +
                    ", dedExpr=" + dedExpr +
                    ", expr=" + expr +
                    ", id=" + id +
                    '}';
        }
    }
    private final Parser parser;

    public MetaBuilder() {
        this.parser = new Parser();
    }

    public List<String> build(List<String> lines) {
        List<Proof> proofs = new ArrayList<>(lines.size());
        Map<Expression, List<Integer>> rightPartOfImplication = new TreeMap<>();
        Map<Expression, List<Integer>> expressions = new TreeMap<>();
        List<String> answer = new ArrayList<>(lines.size());
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

            String comment = "Incorrect";
            // Axiom
            int axiomId;
            if ((axiomId = Axioms.isAxiom(expr)) != -1) {
                comment = "Ax. sch. " + (axiomId + 1);
            }

            int hypothesisId = -1;
            if (axiomId == -1 && (hypothesisId = hypothesesList.indexOf(expr)) != -1) {
                comment = "Hyp. " + (hypothesisId + 1);
            }

            int modus = -1, ponens = -1;
            if (axiomId == -1 && hypothesisId == -1) {
                for (int implI : rightPartOfImplication.getOrDefault(expr, emptyList)) {
                    Proof implicationProof = proofs.get(implI);
                    BinaryOperator impl = (BinaryOperator) implicationProof.expr;
                    if (implicationProof.hyps.equals(hypotheses)) {
                        for (int ai : expressions.getOrDefault(impl.left, emptyList)) {
                            Proof alphaProof = proofs.get(ai);
                            if (alphaProof.hyps.equals(hypotheses)) {
                                modus = ai;
                                ponens = implI;
                                comment = "M.P. " + (modus + 1) + ", " + (ponens + 1);
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
                    if (proof.dedExpr.equals(dedExpr) && proof.dedHyps.equals(dedHypotheses)) {
                        dedId = proofId;
                        comment = "Ded. " + (dedId + 1);
                        break;
                    }
                }
            }

            // adding new params

            // proof
            int id = i;
            BiFunction<List<Integer>, List<Integer>, List<Integer>>
                    mergeLists = (oldL, newL) -> {oldL.addAll(newL); return oldL;};


            proofs.add(new Proof(dedHypotheses, dedExpr, hypotheses, expr, i));

            if (expr instanceof BinaryOperator) {
                BinaryOperator impl = (BinaryOperator) expr;
                if (impl.operator.equals(Operator.IMPL)) {
                    rightPartOfImplication.merge(impl.right, new ArrayList<Integer>() {{add(id);}}, mergeLists);
                }
            }
            expressions.merge(expr, new ArrayList<Integer>() {{add(id);}}, mergeLists);

            answer.add(metaExpression(line, i + 1, comment));
        }

        return answer;
    }

    public static String metaExpression(String expr, int lineId, String args) {
        return "[" + lineId + "] " + expr + " [" + args + "]";
    }
}
