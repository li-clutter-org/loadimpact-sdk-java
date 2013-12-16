package com.loadimpact.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods for lists.
 *
 * @author jens
 */
public class ListUtils {

    /**
     * Call-back interface for {@link #map(java.util.List, com.loadimpact.util.ListUtils.MapClosure)}.
     * @param <From>    value type
     * @param <To>      destination type
     */
    public interface MapClosure<From, To> {
        To eval(From value);
    }

    /**
     * Call-back interface for {@link #reduce(java.util.List, Object, com.loadimpact.util.ListUtils.ReduceClosure)}.
     * @param <Accumulator>     binary accumulator function
     * @param <Value>           element type
     */
    public interface ReduceClosure<Accumulator, Value> {
        Accumulator eval(Accumulator acc, Value value);
    }


    /**
     * Concatenates a list of strings using the given separator. 
     * @param lst           list of text strings
     * @param separator     separator
     * @return string
     */
    public static String join(Collection<String> lst, String separator) {
        StringBuilder buf = new StringBuilder(lst.size() * 64);
        boolean first = true;
        for (String value : lst) {
            if (first) first = false; else buf.append(separator);
            buf.append(value);
        }
        return buf.toString();
    }

    /**
     * Returns the last element of a list, or null if empty.
     * @param lst   the list
     * @param <T>   element type
     * @return last element or null
     */
    public static <T> T last(List<T> lst) {
        if (lst == null || lst.isEmpty()) return null;
        return lst.get(lst.size() - 1);
    }

    /**
     * Applies a function to every item in a list.
     * @param list      the list with values
     * @param f         closure to apply
     * @param <From>    value type
     * @param <To>      destination type
     * @return list of transformed values
     */
    @SuppressWarnings("unchecked")
    public static <From, To> List<To> map(List<From> list, MapClosure<From,To> f) {
        List<To> result = new ArrayList<To>(list.size());
        for (From value : list) {
            result.add( f.eval(value) );
        }
        return result;
    }

    /**
     * Applies a binary function between each element of the given list.
     * @param list      list of elements
     * @param init      initial value for the accumulator
     * @param f         accumulator expression to apply
     * @param <Accumulator>     binary function
     * @param <Value>           element type
     * @return an accumulated/aggregated value
     */
    public static <Accumulator, Value> Accumulator reduce(List<Value> list, Accumulator init, ReduceClosure<Accumulator,Value> f) {
        Accumulator accumulator = init;
        for (Value value : list) {
            accumulator = f.eval(accumulator, value);
        }
        return accumulator;
    }

    /**
     * Computes the average/mean/expected value of a list of numbers.
     * @param values    list of values
     * @return the computed average
     */
    public static double average(List<? extends Number> values) {
        if (values == null || values.isEmpty()) return 0D;

        double sum = 0D;
        for (Number v : values) sum += v.doubleValue();

        return sum / values.size();
    }

    /**
     * Computes the median value of a list of integers.
     * @param values    list of values
     * @return the computed medium
     */
    @SuppressWarnings("unchecked")
    public static int median(List<Integer> values) {
        if (values == null || values.isEmpty()) return 0;

        values = new ArrayList<Integer>(values);
        Collections.sort(values);

        final int size = values.size();
        final int sizeHalf = size / 2;
        if (size % 2 == 1) { //is odd?
            // 0 1 [2] 3 4: size/2 = 5/2 = 2.5 -> 2
            return values.get(sizeHalf);
        }

        // 0 1 [2 3] 4 5: size/2 = 6/2 = 3
        return (values.get(sizeHalf - 1) + values.get(sizeHalf)) / 2;
    }
    
}
