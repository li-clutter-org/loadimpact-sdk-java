package com.loadimpact.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class ListUtilsTest {

    @Test
    public void testJoin() {
        List<String> input = Arrays.asList("foo", "bar", "fee");
        String sep = "#";
        assertThat(ListUtils.join(input,sep), is("foo#bar#fee"));
    }

    @Test
    public void testLast() {
        assertThat(ListUtils.last(Arrays.asList(1,2,3)), is(3));
        assertThat(ListUtils.last(Arrays.asList("a", "b", "c")), is("c"));
        assertThat(ListUtils.last(Arrays.asList(42)), is(42));
        assertThat(ListUtils.last(Collections.EMPTY_LIST), nullValue());
        assertThat(ListUtils.last(null), nullValue());
    }

    @Test
    public void testMap() {
        List<Integer> inputs = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> expected = Arrays.asList(1, 4, 9, 16, 25);
        List<Integer> actual = ListUtils.map(inputs, new ListUtils.MapClosure<Integer, Integer>() {
            public Integer eval(Integer value) {
                return value * value;
            }
        });
        assertThat(actual, is(expected));
    }


    enum E {
        foo, bar, fee;
        public final String p;
        E() { this.p = "_" + name().toUpperCase(); }
    }

    @Test
    public void testMapExtraction() {
        List<String> actual = ListUtils.map(Arrays.asList(E.values()), new ListUtils.MapClosure<E, String>() {
            public String eval(E value) { return value.p; }
        });
        List<String> expected = Arrays.asList("_FOO", "_BAR", "_FEE");
        assertThat(actual, is(expected));
    }

    @Test
    public void testReduce() {
        List<Integer> input = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        int actual = ListUtils.reduce(input, 0, new ListUtils.ReduceClosure<Integer, Integer>() {
            public Integer eval(Integer sum, Integer value) {
                return sum + value;
            }
        });
        assertThat(actual, is(55));
    }

    @Test
    public void testAverage() {
        List<Double> target = Arrays.asList(1.0, 2.0, -10.0, 10.0, 5.0, 4.0);
        // 12 / 6 = 2
        assertThat(ListUtils.average(target), is(2.0));
    }

    @Test
    public void testMedianWithOddNumbers() {
        List<Integer> target = Arrays.asList(1, 2, -10, 10, 5);
        // -10 1 [2] 5 10
        assertThat(ListUtils.median(target), is(2));
    }

    @Test
    public void testMedianWithEvenNumbers() {
        List<Integer> target = Arrays.asList(1, 2, -10, 10, 5, 4);
        // -10 1 [2 4] 5 10
        assertThat(ListUtils.median(target), is(3));
    }

    @Test
    public void testMedianWithTwoNumbers() {
        List<Integer> target = Arrays.asList(-10, 10);
        assertThat(ListUtils.median(target), is(0));
    }

    @Test
    public void testMedianWithOneNumber() {
        List<Integer> target = Arrays.asList(10);
        assertThat(ListUtils.median(target), is(10));
    }

    @Test
    public void testMedianWithZeroNumber() {
        List<Integer> target = Arrays.asList();
        assertThat(ListUtils.median(target), is(0));
    }
}
