package grammar.proof.context;

import grammar.Expression;

import java.util.*;
import java.util.stream.Collectors;

public interface Context {
    /**
     * Converts list to map, where key -- expression in the list,
     * value -- number of occurrences in the list
     *
     * @param list list of expressions
     * @return map made from the list
     */
    static Map<Expression, Integer> toMap(List<Expression> list) {
        return list.stream().collect(Collectors.toMap(
                expr -> expr,
                expr -> 1,
                Integer::sum
        ));
    }

    /**
     * Converts map to list
     *
     * @param map key -- expressions, value -- how many times it will be counted in the new list
     * @return new list of expressions
     */
    static List<Expression> toList(Map<Expression, Integer> map) {
        return map.entrySet()
                .stream()
                .map(entry -> Collections.nCopies(entry.getValue(), entry.getKey()))
                .reduce(new ArrayList<>(), (left, right) -> {
                            left.addAll(right);
                            return left;
                        }
                );
    }

    static <T> List<T> mergeLists(List<T> first, List<T> second) {
        return new ArrayList<T>(first) {{
            addAll(second);
        }};
    }

    static <T> Map<T, Integer> mergeMaps(Map<T, Integer> first, Map<T, Integer> second) {
        return new HashMap<T, Integer>(first) {{
            second.forEach((key, value) -> this.merge(key, value, Integer::sum));
        }};
    }

    /**
     * Returns immutable empty context, singleton object
     *
     * @return empty immutable context
     */
    static Context empty() {
        return ImmutableContext.empty();
    }

    /**
     * Returns immutable context, which contains expressions
     *
     * @param expressions expressions in context
     * @return new immutable context with these expressions
     */
    static Context of(Expression... expressions) {
        return ImmutableContext.of(expressions);
    }

    void add(Expression... expressions);

    void add(List<Expression> list);

    void add(Context context);

    void remove(Expression... expressions);

    void remove(List<Expression> list);

    void remove(Context context);

    Context merge(Context context);

    Context merge(List<Expression> list);

    Context merge(Expression... expressions);

    Context diff(Context context);

    Context diff(List<Expression> list);

    Context diff(Expression... expressions);

    Map<Expression, Integer> getMap();

    List<Expression> getList();

    boolean contains(Expression expression);
}
