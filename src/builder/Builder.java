package builder;

import java.util.List;

public interface Builder<T, R> {
    List<R> build(List<T> input);
}
