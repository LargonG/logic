//package grammar.proof.context;
//
//import grammar.Expression;
//import grammar.operators.Operator;
//import parser.Parser;
//
//import java.util.*;
//
//public class ImmutableContext extends Context {
//    public static final ImmutableContext EMPTY = new ImmutableContext(Collections.emptyList());
//
//    private ImmutableContext(final List<Expression> list,
//                             final boolean doCopy) {
//        super(Collections.unmodifiableList(doCopy ? new ArrayList<>(list) : list));
//    }
//
//    private ImmutableContext(final Map<Expression, Integer> map,
//                             final boolean doCopy) {
//        super(Collections.unmodifiableMap(doCopy ? new HashMap<>(map): map));
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
//    public ImmutableContext(final Context context,
//                            final List<Expression> list) {
//        super();
//        if (context.createdList) {
//            this.list = Collections.unmodifiableList(
//                mergeLists(context.list, list)
//            );
//            this.createdList = true;
//        } else {
//            this.map = Collections.unmodifiableMap(
//                    mergeMaps(context.map, toMap(list))
//            );
//            this.createdMap = true;
//        }
//    }
//
//    public ImmutableContext(final Context context,
//                            final Map<Expression, Integer> map) {
//        super();
//        if (context.createdList) {
//            this.list = Collections.unmodifiableList(
//                    mergeLists(context.list, toList(map))
//            );
//            this.createdList = true;
//        } else {
//            this.map = Collections.unmodifiableMap(
//                    mergeMaps(context.map, map)
//            );
//            this.createdMap = true;
//        }
//    }
//
//    public ImmutableContext(final Context context) {
//        super();
//        if (context.createdList) {
//            this.list = context instanceof ImmutableContext ?
//                    context.list : Collections.unmodifiableList(new ArrayList<>(context.list));
//            this.createdList = true;
//        }
//        if (context.createdMap) {
//            this.map = context instanceof ImmutableContext ?
//                    context.map : Collections.unmodifiableMap(new HashMap<>(context.map));
//            this.createdMap = true;
//        }
//    }
//
//    @Override
//    protected List<Expression> createList() {
//        return Collections.unmodifiableList(toList(map));
//    }
//
//    @Override
//    protected Map<Expression, Integer> createMap() {
//        return Collections.unmodifiableMap(toMap(list));
//    }
//
//    @Override
//    public ImmutableContext getFullContext(Expression expression) {
//        List<Expression> sep = Expression.separate(expression, Operator.IMPL);
//        return new ImmutableContext(this, sep.subList(0, sep.size() - 1));
//    }
//
//    @Override
//    public Context add(Expression... elements) {
//        throw new UnsupportedOperationException("Cannot modify immutable context");
//    }
//
//    @Override
//    public Context add(Context context) {
//        throw new UnsupportedOperationException("Cannot modify immutable context");
//    }
//
//    @Override
//    public Context remove(Expression... elements) {
//        throw new UnsupportedOperationException("Cannot modify immutable context");
//    }
//
//    @Override
//    public Context remove(Context context) {
//        throw new UnsupportedOperationException("Cannot modify immutable context");
//    }
//
//    @Override
//    public ImmutableContext merge(Expression... elements) {
//        Map<Expression, Integer> mp = new HashMap<>(getMap());
//        for (Expression expression: elements) {
//            mp.merge(expression, 1, Integer::sum);
//        }
//        return new ImmutableContext(mp, false);
//    }
//
//    @Override
//    public ImmutableContext merge(Context context) {
//        if (context.createdList) {
//            return new ImmutableContext(this, context.list);
//        }
//        return new ImmutableContext(this, context.map);
//    }
//
//    @Override
//    public ImmutableContext diff(Expression... elements) {
//        Map<Expression, Integer> mp = new HashMap<>(getMap());
//        for (Expression expression: elements) {
//            mp.merge(expression, -1, Integer::sum);
//            if (mp.get(expression) <= 0) {
//                mp.remove(expression);
//            }
//        }
//        return new ImmutableContext(mp, false);
//    }
//
//    @Override
//    public ImmutableContext diff(Context context) {
//        Map<Expression, Integer> values = new HashMap<>(getMap());
//        for (Map.Entry<Expression, Integer> entry: context.getMap().entrySet()) {
//            values.merge(entry.getKey(), -entry.getValue(), Integer::sum);
//            if (values.get(entry.getKey()) <= 0) {
//                values.remove(entry.getKey());
//            }
//        }
//        return new ImmutableContext(values, false);
//    }
//
//    public static ImmutableContext empty() {
//        return EMPTY;
//    }
//
//    public static ImmutableContext of(Expression... expressions) {
//        return new ImmutableContext(Arrays.asList(expressions), false);
//    }
//
//    public static ImmutableContext diff(final ImmutableContext first,
//                                        final ImmutableContext second) {
//        return first.diff(second);
//    }
//}
