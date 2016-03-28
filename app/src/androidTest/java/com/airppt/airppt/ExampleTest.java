package com.airppt.airppt;

import android.test.InstrumentationTestCase;

/**
 * Created by italkbb on 2016/2/29.
 */
public class ExampleTest extends InstrumentationTestCase {

    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}
