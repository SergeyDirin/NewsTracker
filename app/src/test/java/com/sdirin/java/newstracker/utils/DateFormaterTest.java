package com.sdirin.java.newstracker.utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by User on 12.02.2018.
 */
public class DateFormaterTest {

    @Before
    public void setUp() throws Exception {
    }


    @Test
    public void checkFullString() {
        String sDate = "2018-02-11T10:00:00Z";
        try {
            Date d = DateFormater.parse(sDate);
            Assert.assertEquals(sDate,DateFormater.getNetworkString(d));
        } catch (ParseException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void checkShortString() {
        String sDate = "11 фев";
        try {
            Date d = DateFormater.parse(sDate);
            Assert.assertEquals(sDate,DateFormater.getShortString(d));
        } catch (ParseException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}