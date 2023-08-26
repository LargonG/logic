package builder.proof;

import grammar.Expression;
import grammar.operators.Operator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable class that contains context of some proof
 */
public final class Context {
    private final static Context EMPTY = new Context(Collections.emptyList());

    private final List<Expression> list;
    private final Map<Expression, Integer> map;

    private Context(final List<Expression> list, boolean doCopy) {
        this.list = Collections.unmodifiableList(doCopy ? new ArrayList<>(list) : list);
        this.map = Collections.unmodifiableMap(toMap(list));
    }

    private Context(final Map<Expression, Integer> map, boolean doCopy) {
        this.list = Collections.unmodifiableList(toList(map));
        this.map = Collections.unmodifiableMap(doCopy ? new HashMap<>(map) : map);
    }

    public Context(final List<Expression> list) {
        this(list, true);
    }

    public Context(final Map<Expression, Integer> map) {
        this(map, true);
    }

    public Context(final Context context,
                   final List<Expression> list) {
        this.list = Collections.unmodifiableList(
                new ArrayList<Expression>(context.list) {{
                    addAll(list);
                }}
        );
        this.map = Collections.unmodifiableMap(toMap(this.list));
    }

    public Context(final Context context,
                   final Map<Expression, Integer> map) {
        this.map = Collections.unmodifiableMap(
                new HashMap<Expression, Integer>(context.map) {{
                    map.forEach((key, value) -> this.merge(key, value, Integer::sum));
                }}
        );
        this.list = Collections.unmodifiableList(toList(this.map));
    }

    /**
     * Gets the expression & make the new context until expr != left -> right,
     * where all left parts are added to the context
     * @param expression some expression
     * @return context, that contains all left parts of expression implication operator
     */
    public Context getFullContext(Expression expression) {
        List<Expression> sep = Expression.separate(expression, Operator.IMPL);
        return new Context(this, sep.subList(0, sep.size() - 1));
    }

    public Context add(Expression expression) {
        Map<Expression, Integer> mp = new HashMap<>(map);
        mp.merge(expression, 1, Integer::sum);
        return new Context(mp, false);
    }

    public Context remove(Expression... expressions) {
        Map<Expression, Integer> mp = new HashMap<>(map);
        for (Expression expression: expressions) {
            mp.merge(expression, -1, Integer::sum);
            if (mp.get(expression) <= 0) {
                mp.remove(expression);
            }
        }
        return new Context(mp, false);
    }

    public boolean contains(Expression expression) {
        return map.containsKey(expression);
    }

    public int indexOf(Expression expression) {
        return list.indexOf(expression);
    }

    public List<Expression> getList() {
        return list;
    }

    public Map<Expression, Integer> getMap() {
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Context context = (Context) o;
        return Objects.equals(map, context.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    @Override
    public String toString() {
        return list.stream()
                .map(Expression::suffixString)
                .reduce((left, right) -> left + "," + right).orElse("");
    }

    public static Context merge(final Context first, final Context second) {
        return new Context(first, second.list);
    }

    private static <T> List<T> toList(Map<T, Integer> map) {
        return map.entrySet()
                .stream()
                .map(entry -> Collections.nCopies(entry.getValue(), entry.getKey()))
                .reduce(new ArrayList<>(), (left, right) -> { left.addAll(right); return left; }
                );
    }

    private static <T> Map<T, Integer> toMap(List<T> list) {
        return list.stream().collect(Collectors.toMap(
                expr -> expr,
                expr -> 1,
                Integer::sum
        ));
    }

    public static Context empty() {
        return EMPTY;
    }

    public static Context of(Expression... expressions) {
        if (expressions.length == 0) {
            return empty();
        }
        return new Context(Arrays.asList(expressions), false);
    }
}
