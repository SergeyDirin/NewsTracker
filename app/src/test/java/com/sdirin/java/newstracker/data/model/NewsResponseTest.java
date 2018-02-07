package com.sdirin.java.newstracker.data.model;

import com.sdirin.java.newstracker.data.model.parse.NewsParser;

import junit.framework.Assert;

import org.junit.Test;

import static com.sdirin.java.newstracker.data.network.mock.Requests.ARTICLES_JSON;
import static com.sdirin.java.newstracker.data.network.mock.Requests.ARTICLES_JSON_NEW;

/**
 * Created by SDirin on 05-Jan-18.
 */
public class NewsResponseTest {

    @Test
    public void combineWith() throws Exception {
        NewsResponse newsResponse = NewsParser.fromJson(ARTICLES_JSON);
        NewsResponse newsResponseToAdd = NewsParser.fromJson(ARTICLES_JSON_NEW);

        Assert.assertEquals(2,newsResponse.getArticles().size());
        Assert.assertEquals(2,newsResponseToAdd.getArticles().size());

        newsResponse.combineWith(newsResponseToAdd);

        Assert.assertEquals(3,newsResponse.getArticles().size());
    }

    @Test
    public void combineWithEmpty() throws Exception {
        NewsResponse newsResponse = NewsParser.fromJson(ARTICLES_JSON);
        NewsResponse newsResponseToAdd = new NewsResponse();

        Assert.assertEquals(2,newsResponse.getArticles().size());

        newsResponse.combineWith(newsResponseToAdd);

        Assert.assertEquals(2,newsResponse.getArticles().size());
    }

    @Test
    public void emptyCombineWith() throws Exception {
        NewsResponse newsResponse = new NewsResponse();
        NewsResponse newsResponseToAdd = NewsParser.fromJson(ARTICLES_JSON);

        Assert.assertEquals(2,newsResponseToAdd.getArticles().size());

        newsResponse.combineWith(newsResponseToAdd);

        Assert.assertEquals(2,newsResponse.getArticles().size());
    }

}