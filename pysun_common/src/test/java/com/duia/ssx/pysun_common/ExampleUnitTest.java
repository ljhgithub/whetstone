package com.duia.ssx.pysun_common;

import com.pysun.common.utils.DateUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void timeStampTrans(){
        String s = DateUtils.long2String(1539273600000L,DateUtils.DATE_FORMAT.DATE_SECONDS);
        System.out.print(s);
    }
}