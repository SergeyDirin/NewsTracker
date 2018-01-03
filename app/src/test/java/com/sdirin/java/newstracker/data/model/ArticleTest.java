package com.sdirin.java.newstracker.data.model;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by SDirin on 02-Jan-18.
 */
public class ArticleTest {

    @Test
    public void checkSettingPublishedAt() {
        String stringDate = "2018-01-02T07:36:33Z";
        String stringDateResult = "2 Jan";
        Date date = new Date();
        Article article = new Article();

        try {
            article.setPublishedAtString(stringDate);
        } catch (ParseException e) {
            throw new AssertionError("Parse error - "+e.getMessage());
        }

        Assert.assertEquals(stringDateResult,article.getPublishedAtString());
    }

}