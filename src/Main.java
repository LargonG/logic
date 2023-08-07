import builder.MetaBuilder;
import generator.Generator;
import generator.MetaGenerator;
import parser.*;
import resolver.Axioms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // checkAxioms(100, 2);
        // test(100000);
        // System.out.println("End testing");
        Scanner scanner = new Scanner(System.in);
        List<String> input = new ArrayList<>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if (line.equals("0")) {
                break;
            }
            input.add(line);
        }
        List<String> result = new MetaBuilder().build(input);
        for (String res : result) {
            System.out.println(res);
        }
    }

    public static void test(int testsCount) {
        Generator generator = new Generator(0);
        Parser parser = new Parser();
        for (int it = 0; it < testsCount; it++) {
            Expression before = generator.generate(2);
            String expr = before.suffixString(null);
            Expression after = parser.parse(expr);
            if (before.toString().equals(after.toString())) {
                System.out.println("OK");
                System.out.println("Before: " + before);
                System.out.println("After: " + after);
                System.out.println("Suffix: " + before.suffixString(null));
            } else {
                System.out.println("ERROR!");
                System.out.println("Before: " + before);
                System.out.println("After: " + after);
                System.out.println("Suffix before: " + before.suffixString(null));
                System.out.println("Suffix after:  " + after.suffixString(null));
                break;
            }
        }
    }

    public static void checkAxioms(int n, int len) {
        String[] axioms = new String[] {
                "A->B->A",
                "A->(A->B->A)->A",
                "A|B->B&A->A|B",
                "A&Y->(R&Y->Z)->A&Y",
                "((A->A)->B)->((A->A)->B->(Y|W))->((A->A)->(Y|W))"
        };
        Parser parser = new Parser();
        for (String axiom: axioms) {
            if (Axioms.isAxiom(parser.parse(axiom)) != -1) {
                System.out.println("OK");
            } else {
                System.out.println("ERROR");
            }
        }

        MetaGenerator generator = new MetaGenerator();
        MetaBuilder builder = new MetaBuilder();
        for (int i = 0; i < n; i++) {
            MetaGenerator.Test test = generator.generate(len);
            List<String> res = builder.build(Arrays.asList(test.lines));
            boolean ok = true;
            for (int j = 0; j < res.size(); j++) {
                if (!res.get(j).equals(test.result[j])) {
                    ok = false;
                }
            }

            if (!ok) {
                System.out.println("ERROR");
                System.out.println(Arrays.toString(test.lines));
                System.out.println("expected:");
                System.out.println(Arrays.toString(test.result));
                System.out.println("actual:");
                System.out.println(Arrays.toString(res.toArray()));
                break;
            } else {
                System.out.println("OK");
                System.out.println(Arrays.toString(test.lines));
                System.out.println(Arrays.toString(res.toArray()));
            }
        }
    }
}