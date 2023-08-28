package tasks;

import parser.ExpressionParser;

import java.util.Scanner;

public class TaskA implements Task {
    public static void main(String[] args) {
        new TaskA().solution(args);
    }

    @Override
    public void solution(String... args) {
        Scanner scanner = new Scanner(System.in);

        String line = scanner.nextLine();

        System.out.println(new ExpressionParser().parse(line));
    }
}
