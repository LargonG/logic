//package grammar.proof.context;
//
//import grammar.Expression;
//import grammar.operators.Operator;
//
//import java.util.*;
//
//public class MutableContext extends Context {
//    private MutableContext(final List<Expression> list,
//                           final boolean doCopy) {
//        super(doCopy ? new ArrayList<>(list) : list);
//    }
//
//    private MutableContext(final Map<Expression, Integer> map,
//                           final boolean doCopy) {
//        super(doCopy ? new HashMap<>(map) : map);
//    }
//
//    public MutableContext(final Context context,
//                            final List<Expression> list) {
//        super();
//        if (context.createdList) {
//            this.list = mergeLists(context.list, list);
//            this.createdList = true;
//        } else {
//            this.map = mergeMaps(context.map, toMap(list));
//            this.createdMap = true;
//        }
//    }
//
//    public MutableContext(final Context context,
//                            final Map<Expression, Integer> map) {
//        super();
//        if (context.createdList) {
//            this.list = mergeLists(context.list, toList(map));
//            this.createdList = true;
//        } else {
//            this.map = mergeMaps(context.map, map);
//            this.createdMap = true;
//        }
//    }
//
//    public MutableContext(final Context context) {
//        super();
//        if (context.createdList) {
//            this.list = new ArrayList<>(context.list);
//            this.createdList = true;
//        }
//        if (context.createdMap) {
//            this.map = new HashMap<>(context.map);
//            this.createdMap = true;
//        }
//    }
//
//    public MutableContext(final List<Expression> list) {
//        this(list, true);
//    }
//
//    public MutableContext(final Map<Expression, Integer> map) {
//        this(map, true);
//    }
//
//    @Override
//    protected List<Expression> createList() {
//        return toList(this.map);
//    }
//
//    @Override
//    protected Map<Expression, Integer> createMap() {
//        return toMap(this.list);
//    }
//
//    @Override
//    public MutableContext getFullContext(Expression expression) {
//        List<Expression> sep = Expression.separate(expression, Operator.IMPL);
//        return new MutableContext(this, sep.subList(0, sep.size() - 1));
//    }
//
//    @Override
//    public Context add(Expression... elements) {
//        getMap();
//        dropList();
//
//        for (Expression expression: elements) {
//            map.merge(expression, 1, Integer::sum);
//        }
//        return this;
//    }
//
//    @Override
//    public Context add(Context context) {
//        getMap();
//        dropList();
//
//        if (context.createdMap) {
//            for (Map.Entry<Expression, Integer> entry: context.map.entrySet()) {
//                map.merge(entry.getKey(), entry.getValue(), Integer::sum);
//            }
//        } else {
//            for (Expression expression: context.list) {
//                map.merge(expression, 1, Integer::sum);
//            }
//        }
//        return this;
//    }
//
//    @Override
//    public Context remove(Expression... elements) {
//        getMap();
//        dropList();
//
//        for (Expression expression: elements) {
//            map.merge(expression, -1, Integer::sum);
//            removeIfZero(expression);
//        }
//
//        return this;
//    }
//
//    @Override
//    public Context remove(Context context) {
//        getMap();
//        dropList();
//
//        if (context.createdMap) {
//            for (Map.Entry<Expression, Integer> entry: context.map.entrySet()) {
//                map.merge(entry.getKey(), -entry.getValue(), Integer::sum);
//                removeIfZero(entry.getKey());
//            }
//        } else {
//            for (Expression expression: context.list) {
//                map.merge(expression, -1, Integer::sum);
//                removeIfZero(expression);
//            }
//        }
//        return this;
//    }
//
//    private void removeIfZero(Expression expression) {
//        if (map.get(expression) <= 0) {
//            map.remove(expression);
//        }
//    }
//
//    @Override
//    public Context merge(Expression... elements) {
//        return new MutableContext(this, Arrays.asList(elements));
//    }
//
//    @Override
//    public Context merge(Context context) {
//        getMap();
//
//        if (context.createdMap) {
//            return new MutableContext(this, context.map);
//        }
//        return new MutableContext(this, context.list);
//    }
//
//    @Override
//    public Context diff(Expression... elements) {
//        Context context = new MutableContext(this);
//        context.remove(elements);
//        return context;
//    }
//
//    @Override
//    public Context diff(Context context) {
//        Context newContext = new MutableContext(this);
//        newContext.remove(context);
//        return newContext;
//    }
//
//    public static MutableContext empty() {
//        return new MutableContext(new HashMap<>());
//    }
//
//    public static MutableContext of(Expression... expressions) {
//        return new MutableContext(Arrays.asList(expressions));
//    }
//}
