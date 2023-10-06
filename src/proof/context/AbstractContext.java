package proof.context;

import grammar.Expression;

import java.util.*;

public abstract class AbstractContext implements Context {
    protected final Map<Expression, Integer> map;

    protected AbstractContext(Map<Expression, Integer> map, boolean doCopy) {
        this.map = doCopy ? new HashMap<>(map) : map;
    }

    public AbstractContext(Map<Expression, Integer> map) {
        this(map, true);
    }

    public AbstractContext(List<Expression> list) {
        this(Context.toMap(list), false);
    }

    public AbstractContext(Expression... expressions) {
        this(Context.toMap(Arrays.asList(expressions)), false);
    }

    @Override
    public boolean contains(Expression expression) {
        return map.containsKey(expression);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractContext that = (AbstractContext) o;
        return Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    @Override
    public String toString() {
        return getList().stream()
                .map(Expression::suffixString)
                .reduce((left, right) -> left + "," + right).orElse("");
    }
}
