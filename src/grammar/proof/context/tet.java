//package grammar.proof.context;
//
//import grammar.Expression;
//import grammar.operators.Operator;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static grammar.proof.context.Context.toList;
//import static grammar.proof.context.Context.toMap;
//
///**
// * Immutable class that contains context of some proof
// */
//public final class ImmutableContext implements Context {
//    private final static ImmutableContext EMPTY = new ImmutableContext(Collections.emptyList());
//
//    private final List<Expression> list;
//    private final Map<Expression, Integer> map;
//
//    private ImmutableContext(final List<Expression> list, boolean doCopy) {
//        this.list = Collections.unmodifiableList(doCopy ? new ArrayList<>(list) : list);
//        this.map = Collections.unmodifiableMap(toMap(list));
//    }
//
//    private ImmutableContext(final Map<Expression, Integer> map, boolean doCopy) {
//        this.list = Collections.unmodifiableList(toList(map));
//        this.map = Collections.unmodifiableMap(doCopy ? new HashMap<>(map) : map);
//    }
//
//    public ImmutableContext(final List<Expression> list) {
//        this(list, true);
//    }
//
//    public ImmutableContext(final Map<Expression, Integer> map) {
//        this(map, true);
//    }
//
//    public ImmutableContext(final ImmutableContext immutableContext,
//                            final List<Expression> list) {
//        this.list = Collections.unmodifiableList(
//                new ArrayList<Expression>(immutableContext.list) {{
//                    addAll(list);
//                }}
//        );
//        this.map = Collections.unmodifiableMap(toMap(this.list));
//    }
//
//    public ImmutableContext(final ImmutableContext immutableContext,
//                            final Map<Expression, Integer> map) {
//        this.map = Collections.unmodifiableMap(
//                new HashMap<Expression, Integer>(immutableContext.map) {{
//                    map.forEach((key, value) -> this.merge(key, value, Integer::sum));
//                }}
//        );
//        this.list = Collections.unmodifiableList(toList(this.map));
//    }
//
//    /**
//     * Gets the expression & make the new context until expr != left -> right,
//     * where all left parts are added to the context
//     * @param expression some expression
//     * @return context, that contains all left parts of expression implication operator
//     */
//    public ImmutableContext getFullContext(Expression expression) {
//        List<Expression> sep = Expression.separate(expression, Operator.IMPL);
//        return new ImmutableContext(this, sep.subList(0, sep.size() - 1));
//    }
//
//    public ImmutableContext add(Expression expression) {
//        Map<Expression, Integer> mp = new HashMap<>(map);
//        mp.merge(expression, 1, Integer::sum);
//        return new ImmutableContext(mp, false);
//    }
//
//    public ImmutableContext remove(Expression... expressions) {
//        Map<Expression, Integer> mp = new HashMap<>(map);
//        for (Expression expression: expressions) {
//            mp.merge(expression, -1, Integer::sum);
//            if (mp.get(expression) <= 0) {
//                mp.remove(expression);
//            }
//        }
//        return new ImmutableContext(mp, false);
//    }
//
//    public boolean contains(Expression expression) {
//        return map.containsKey(expression);
//    }
//
//    public int indexOf(Expression expression) {
//        return list.indexOf(expression);
//    }
//
//    public List<Expression> getList() {
//        return list;
//    }
//
//    public Map<Expression, Integer> getMap() {
//        return map;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        ImmutableContext immutableContext = (ImmutableContext) o;
//        return Objects.equals(map, immutableContext.map);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(map);
//    }
//
//    @Override
//    public String toString() {
//        return list.stream()
//                .map(Expression::suffixString)
//                .reduce((left, right) -> left + "," + right).orElse("");
//    }
//
//    public static ImmutableContext merge(final ImmutableContext first, final ImmutableContext second) {
//        return new ImmutableContext(first, second.list);
//    }
//
//    public static ImmutableContext diff(final ImmutableContext first, final ImmutableContext second) {
//        Map<Expression, Integer> values = new HashMap<>(first.getMap());
//        for (Map.Entry<Expression, Integer> entry: second.getMap().entrySet()) {
//            values.merge(entry.getKey(), -entry.getValue(), Integer::sum);
//            if (values.get(entry.getKey()) <= 0) {
//                values.remove(entry.getKey());
//            }
//        }
//        return new ImmutableContext(values, false);
//    }
//
//
//    public static ImmutableContext empty() {
//        return EMPTY;
//    }
//
//    public static ImmutableContext of(Expression... expressions) {
//        if (expressions.length == 0) {
//            return empty();
//        }
//        return new ImmutableContext(Arrays.asList(expressions), false);
//    }
//}
