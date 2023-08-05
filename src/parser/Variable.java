package parser;

import java.util.Map;

public class Variable implements Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean calculate(Map<String, Boolean> values) {
        return values.get(name);
    }

    @Override
    public String suffixString(Operator before) {
        return name;
    }
}
