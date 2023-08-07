package builder;

import parser.*;
import resolver.Axioms;
import resolver.Hypothesis;
import sun.reflect.generics.tree.Tree;

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

//            if (axiomId == -1 && hypothesisId == -1) {
//                for (int j = 0; j < proofs.size(); j++) {
//                    Proof proof = proofs.get(j);
//                    if (proof.expr instanceof BinaryOperator
//                            && proof.hyps.equals(hypotheses)) {
//                        BinaryOperator impl = (BinaryOperator) proof.expr;
//                        if (impl.operator == Operator.IMPL && impl.right.equals(expr)) {
//                            Expression left = impl.left;
//                            for (int id: expressions.getOrDefault(left, new ArrayList<>())) {
//                                if (proofs.get(id).hyps.equals(hypotheses)) {
//                                    modus = id;
//                                    ponens = j;
//                                    comment = "M.P. " + (id + 1) + ", " + (j + 1);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    if (modus != -1) break;
//                }
//            }

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
//        List<String> result = new ArrayList<>();
//        List<Proof> proofs = new ArrayList<>();
//        Map<Expression, List<Integer>> exprs = new HashMap<>();
//        for (int i = 0; i < lines.size(); i++) {
//            String[] t = lines.get(i).split("[|]-");
//            Map<Expression, Integer> hyps = new HashMap<>();
//            List<Expression> context = new ArrayList<>();
//            for (String str : t[0].split(",")) {
//                if (str.isEmpty()) {
//                    continue;
//                }
//                Expression e = parser.parse(str);
//                hyps.merge(e, 1, Integer::sum);
//                context.add(e);
//            }
//            Expression expr = parser.parse(t[1]);
//            String args = "Incorrect";
//            int axiomId;
//            boolean axiom = false;
//            if ((axiomId = Axioms.isAxiom(expr)) != -1) {
//                axiom = true;
//                args = "Ax. sch. " + (axiomId + 1);
//            }
//
//            boolean hypothesis = false;
//            for (int j = 0; j < hyps.size(); j++) {
//                if (context.get(j).equals(expr)) {
//                    hypothesis = true;
//                    args = "Hyp. " + (j + 1);
//                    break;
//                }
//            }
//
//            boolean modus = false;
//            // Modus Ponens
//            if (!axiom && !hypothesis) {
//                for (int j = 0; j < proofs.size(); j++) {
//                    Proof proof = proofs.get(j);
//                    if (proof.expr instanceof BinaryOperator
//                            && proof.hyps.equals(hyps)) {
//                        BinaryOperator impl = (BinaryOperator) proof.expr;
//                        if (impl.operator == Operator.IMPL && impl.right.equals(expr)) {
//                            Expression left = impl.left;
//                            for (int id: exprs.getOrDefault(left, new ArrayList<>())) {
//                                if (proofs.get(id).hyps.equals(hyps)) {
//                                    modus = true;
//                                    args = "M.P. " + (id + 1) + ", " + (j + 1);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                    if (modus) break;
//                }
//            }
//
//            Expression dedExpr = expr;
//            Map<Expression, Integer> dedContext = new HashMap<>(hyps);
//            // Deduction
//            while (dedExpr instanceof BinaryOperator) {
//                BinaryOperator impl = (BinaryOperator) dedExpr;
//                if (impl.operator == Operator.IMPL) {
//                    dedContext.merge(impl.left, 1, Integer::sum);
//                    dedExpr = impl.right;
//                } else {
//                    break;
//                }
//            }
//
//            if (!axiom && !hypothesis && !modus) {
//                for (int j = 0; j < proofs.size(); j++) {
//                    Proof proof = proofs.get(j);
//                    if (proof.dedHyps.equals(dedContext) && proof.dedExpr.equals(dedExpr)) {
//                        args = "Ded. " + (j + 1);
//                        break;
//                    }
//                }
//            }
//
//            result.add(metaExpression(lines.get(i), i + 1, args));
//            int finalI = i;
//            exprs.merge(expr, new ArrayList<Integer>() {{add(finalI);}}, (oldL, newL) -> {oldL.addAll(newL); return oldL;});
//            proofs.add(new Proof(dedContext, dedExpr, hyps, expr, i));
//        }
//
//        return result;
    }

    public static String metaExpression(String expr, int lineId, String args) {
        return "[" + lineId + "] " + expr + " [" + args + "]";
    }
}
