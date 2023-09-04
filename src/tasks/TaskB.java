package tasks;

import grammar.proof.GProof;
import parser.ProofParser;

import java.util.List;
import java.util.stream.Collectors;

public class TaskB implements Task {
    public static void main(String[] args) {
        new TaskB().solution(args);
    }

    @Override
    public void solution(String... args) {
        List<String> lines = Util.getLines(System.in);
        ProofParser parser = new ProofParser();
        List<GProof> proofs = GProof.addMeta(
                lines.stream()
                        .map(parser::parse)
                        .collect(Collectors.toList())
        );

        for (GProof proof: proofs) {
            System.out.println(proof);
        }
    }
}
