package grammar;

import parser.Parser;

import java.util.Map;
import java.util.Objects;

public class Scheme implements Expression {
    private static final Parser parser = new Parser();

    public final String name;

    public Scheme(String name) {
        this.name = name;
    }

    @Override
    public boolean calculate(Map<String, Boolean> values) {
        throw new UnsupportedOperationException("Cannot calculate scheme");
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String suffixString(Operator before, boolean brackets) {
        return name;
    }

    @Override
    public Expression paste(Map<String, Expression> values) {
        return values.get(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scheme scheme = (Scheme) o;
        return Objects.equals(name, scheme.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(Expression o) {
        if (o instanceof Scheme) {
            Scheme sh = (Scheme) o;
            return name.compareTo(sh.name);
        }
        return 1;
    }

    public static Expression create(String expression, Map<String, Expression> values) {
        return parser.parse(expression).paste(values);
    }
}
