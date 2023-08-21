package builder;

import java.util.List;

/**
 * Abstract builder
 * @param <T> input
 * @param <R> output
 */
public interface Builder<T, R> {
    List<R> build(List<T> input);
}
