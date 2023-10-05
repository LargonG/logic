package tasks;

import grammar.Expression;
import parser.ExpressionParser;
import parser.Parser;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TaskE implements Task {
    public static void main(String[] args) {
        new TaskE().solution(args);
    }

    @Override
    public void solution(String... args) {
        String line = Util.getLine(System.in);
        Parser<Expression> parser = new ExpressionParser();
        Expression expression = parser.parse(line);
        Expression.PreliminaryForm result = expression.toPreliminaryForm();
        PrintWriter out = new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(FileDescriptor.out),
                                StandardCharsets.UTF_8),
                        512));

        result.from.printProofsTree(out);
        out.flush();
        System.out.println();
        result.to.printProofsTree(out);
        out.flush();
    }
}
