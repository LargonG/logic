package builder;

import builder.descriptions.*;
import parser.BinaryOperator;
import parser.Expression;
import parser.Operator;
import parser.Parser;
import resolver.Axioms;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Builds meta proof (adds description)
 */
public class MetaBuilder implements Builder<String, Proof> {
    private final Parser parser;

    public MetaBuilder() {
        this.parser = new Parser();
    }

    public List<Proof> build(final List<String> lines) {
        List<Proof> proofs = new ArrayList<>(lines.size());
        Map<Expression, List<Integer>> rightPartOfImplication = new TreeMap<>();
        Map<Expression, List<Integer>> expressions = new TreeMap<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] ln = line.split("[|]-");
            List<Expression> hypothesesList = Arrays.stream(ln[0].split(","))
                    .map(parser::parse)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            Proof current = new Proof(parser.parse(ln[1]), hypothesesList, i);
            Description comment = new Incorrect();

            // Axiom
            int axiomId;
            if ((axiomId = Axioms.isAxiom(current.expression)) != -1) {
                comment = new AxiomScheme(axiomId);
            }

            // Hypothesis
            int hypothesisId = -1;
            if (axiomId == -1 && (hypothesisId = hypothesesList.indexOf(current.expression)) != -1) {
                comment = new Hypothesis(hypothesisId);
            }

            // Modus Ponens
            int modus = -1, ponens = -1;
            if (axiomId == -1 && hypothesisId == -1) {
                for (int implI : rightPartOfImplication.getOrDefault(current.expression, Collections.emptyList())) {
                    Proof implicationProof = proofs.get(implI);
                    BinaryOperator impl = (BinaryOperator) implicationProof.expression;
                    if (implicationProof.getContext().equals(current.getContext())) {
                        for (int ai : expressions.getOrDefault(impl.left, Collections.emptyList())) {
                            Proof alphaProof = proofs.get(ai);
                            if (alphaProof.getContext().equals(current.getContext())) {
                                modus = ai;
                                ponens = implI;
                                comment = new ModusPonens(proofs, modus, ponens);
                            }
                        }
                    }
                }
            }

            // Deduction
            int dedId = -1;
            if (axiomId == -1 && hypothesisId == -1 && modus == -1 && ponens == -1) {
                for (int proofId = 0; proofId < proofs.size(); proofId++) {
                    Proof proof = proofs.get(proofId);
                    if (proof.getDeductionExpression().equals(current.getDeductionExpression())
                            && proof.getDeductionContext().equals(current.getDeductionContext())) {
                        dedId = proofId;
                        comment = new Deduction(proofs, dedId);
                        break;
                    }
                }
            }

            // adding new params

            // proof
            int id = i;
            BiFunction<List<Integer>, List<Integer>, List<Integer>>
                    mergeLists = (oldL, newL) -> {oldL.addAll(newL); return oldL;};

            current.description = comment;
            proofs.add(current);
            List<Expression> impl = Expression.separate(current.expression, Operator.IMPL, 1);
            if (impl.size() > 1) {
                rightPartOfImplication.merge(impl.get(1), new ArrayList<>(Collections.singletonList(id)), mergeLists);
            }
            expressions.merge(current.expression, new ArrayList<>(Collections.singletonList(id)), mergeLists);
        }

        return proofs;
    }
}
