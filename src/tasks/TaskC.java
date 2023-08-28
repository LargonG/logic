package tasks;

import parser.Parser;
import parser.ProofParser;
import grammar.proof.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TaskC implements Task {
    public static void main(String[] args) {
        new TaskC().solution(args);
    }

    @Override
    public void solution(String... args) {
        List<String> lines = Util.getLines(System.in);
        Parser<Proof> parser = new ProofParser();
        List<Proof> pr = lines.stream().map(parser::parse).collect(Collectors.toList());
        List<GProof> proofs = GProof.addMeta(pr);
        GProof leaf = proofs.get(proofs.size() - 1);

        List<MetaProof> tree = leaf.unpackDeduction().getProofsTree();

        System.out.println(leaf.getProof());
        for (MetaProof node: tree) {
            System.out.println(node.getProof().getExpression().suffixString());
        }
    }
}
