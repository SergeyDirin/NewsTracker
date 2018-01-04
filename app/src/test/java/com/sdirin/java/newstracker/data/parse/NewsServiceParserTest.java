package com.sdirin.java.newstracker.data.parse;

import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.data.network.mock.Requests;

import junit.framework.Assert;

import org.junit.Test;

import java.text.ParseException;

/**
 * Created by SDirin on 02-Jan-18.
 */
public class NewsServiceParserTest {
    @Test
    public void checkOkJson() {
        NewsResponse response;
        try {
            response = NewsServiceParser.fromJson(Requests.OK_RESP);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new AssertionError("Parse exception = " + e.getMessage());
        }

        Assert.assertNotNull(response);
        Assert.assertEquals("ok",response.getStatus());
        Assert.assertNotNull(response.getArticles());
        Assert.assertEquals(2,response.getArticles().size());
        Assert.assertEquals("Colin Campbell",response.getArticles().get(0).getAuthor());
        Assert.assertEquals("polygon",response.getArticles().get(0).getSource().getId());
    }

    @Test
    public void checkNoApi() {
        NewsResponse response;
        try {
            response = NewsServiceParser.fromJson(Requests.NO_API);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new AssertionError("Parse exception = " + e.getMessage());
        }

        Assert.assertNotNull(response);
        Assert.assertEquals("error",response.getStatus());
        Assert.assertEquals("apiKeyMissing",response.getCode());
    }

    @Test
    public void checkTooManyRequests() {
        NewsResponse response;
        try {
            response = NewsServiceParser.fromJson(Requests.TOO_MANY_REQUESTS);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new AssertionError("Parse exception = " + e.getMessage());
        }

        Assert.assertNotNull(response);
        Assert.assertEquals("error",response.getStatus());
        Assert.assertEquals("sourcesTooMany",response.getCode());
    }

    @Test
    public void checkWrongApiKey() {
        NewsResponse response;
        try {
            response = NewsServiceParser.fromJson(Requests.WRONG_APIKEY);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new AssertionError("Parse exception = " + e.getMessage());
        }

        Assert.assertNotNull(response);
        Assert.assertEquals("error",response.getStatus());
        Assert.assertEquals("apiKeyInvalid",response.getCode());
    }

    @Test
    public void checkOkWithNull() {
        NewsResponse response;
        try {
            response = NewsServiceParser.fromJson(Requests.OK_WITH_NULL);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new AssertionError("Parse exception = " + e.getMessage());
        }

        Assert.assertNotNull(response);
        Assert.assertEquals("ok",response.getStatus());
        Assert.assertNotNull(response.getArticles());
        Assert.assertEquals(2,response.getArticles().size());
        Assert.assertEquals("Colin Campbell",response.getArticles().get(0).getAuthor());
        Assert.assertEquals("Unknown",response.getArticles().get(0).getSource().getId());
    }
}