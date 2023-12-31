package parser;

import grammar.Expression;
import proof.Proof;
import proof.context.LinkedContext;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProofParser implements Parser<Proof> {
    private final Parser<Expression> parser;

    public ProofParser() {
        parser = new ExpressionParser();
    }

    @Override
    public Proof parse(String line) {
        String[] s = line.split("[|]-");
        String[] contextExpressions = s[0].split(",");
        LinkedContext immutableContext = new LinkedContext(Arrays
                .stream(contextExpressions)
                .map(parser::parse)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
        return new Proof(parser.parse(s[1]), immutableContext);
    }
}
