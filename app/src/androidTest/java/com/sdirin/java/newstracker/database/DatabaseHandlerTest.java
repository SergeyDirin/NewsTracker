package com.sdirin.java.newstracker.database;

import android.support.test.InstrumentationRegistry;

import com.sdirin.java.newstracker.data.model.Article;
import com.sdirin.java.newstracker.data.model.Source;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.util.List;

/**
 * Created by SDirin on 05-Jan-18.
 */
public class DatabaseHandlerTest {
    private DatabaseHandler db;

    @Before
    public void setUp(){
        db = new DatabaseHandler(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void tearDown() throws Exception {
        Article dbArticle = db.getArticleByTitle("Test Article Title");
        if (dbArticle != null){
            db.deleteSource(dbArticle.getSource());
            db.deleteArticle(dbArticle);
        }
        dbArticle = db.getArticleByTitle("Test Article UPDATED Title");
        if (dbArticle != null){
            db.deleteSource(dbArticle.getSource());
            db.deleteArticle(dbArticle);
        }
        db.close();
    }

    @Test
    public void checkDbExists() {
        Assert.assertNotNull(db);
    }

    Article createTestArticle(){
        Source source = new Source("TesteSourceId","TestSourse");
        Article article = new Article();
        article.setSource(source);
        article.setAuthor("Test Article Author");
        article.setTitle("Test Article Title");
        article.setDescription("Test Article Description");
        article.setUrl("Test Article Url");
        article.setUrlToImage("Test Article UrlToImage");
        try {
            article.setPublishedAtString("5 Jan");
        } catch (ParseException e) {
            e.printStackTrace();
            Assert.fail("Cannot parse date");
        }
        return article;
    }

    void addArticle(){
        Article article = createTestArticle();
        db.addArticle(article);
    }

    @Test
    public void checkAddArticle() {
        addArticle();
        Article dbArticle = db.getArticleByTitle("Test Article Title");
        if (dbArticle == null){
            Assert.fail("Can not find inserted Article");
        }
        Assert.assertEquals("TesteSourceId",dbArticle.getSource().getId());
        Assert.assertEquals("TestSourse",dbArticle.getSource().getName());
        Assert.assertEquals("Test Article Author",dbArticle.getAuthor());
        Assert.assertEquals("Test Article Description",dbArticle.getDescription());
        Assert.assertEquals("Test Article UrlToImage",dbArticle.getUrlToImage());
        Assert.assertEquals("5 Jan",dbArticle.getPublishedAtString());
    }

    @Test
    public void checkGetAllArticles() {
        addArticle();
        List<Article> list = db.getAllArticles();
        Assert.assertTrue("List size >= 1",list.size()>=1);
        boolean bFound = false;
        for (Article article: list) {
            if (article.getTitle().equals("Test Article Title")){
                bFound = true;
                break;
            }
        }
        Assert.assertTrue("Found inserted Article",bFound);
    }

    @Test
    public void checkUpdateArticle() {
        addArticle();
        Article dbArticle = db.getArticleByTitle("Test Article Title");
        dbArticle.setTitle("Test Article UPDATED Title");
        db.updateArticle(dbArticle);
        dbArticle = db.getArticleByTitle("Test Article UPDATED Title");
        if (dbArticle == null){
            Assert.fail("Can not find inserted Article");
        }
        Assert.assertEquals("TesteSourceId",dbArticle.getSource().getId());
        Assert.assertEquals("TestSourse",dbArticle.getSource().getName());
        Assert.assertEquals("Test Article Author",dbArticle.getAuthor());
        Assert.assertEquals("Test Article Description",dbArticle.getDescription());
        Assert.assertEquals("Test Article UrlToImage",dbArticle.getUrlToImage());
        Assert.assertEquals("5 Jan",dbArticle.getPublishedAtString());
    }

    @Test
    public void checkUpdateNotExistingArticle() {
        Article article = createTestArticle();
        db.updateArticle(article);
        article = db.getArticleByTitle("Test Article Title");
        Assert.assertNull(article);
    }

    @Test
    public void checkUpdateArticleByTitle() {
        addArticle();
        Article dbArticle = db.getArticleByTitle("Test Article Title");
        dbArticle.setDbId(-1);
        dbArticle.setTitle("Test Article UPDATED Title");
        db.updateArticle(dbArticle);
        dbArticle = db.getArticleByTitle("Test Article UPDATED Title");
        if (dbArticle == null){
            Assert.fail("Can not find inserted Article");
        }
        Assert.assertEquals("TesteSourceId",dbArticle.getSource().getId());
        Assert.assertEquals("TestSourse",dbArticle.getSource().getName());
        Assert.assertEquals("Test Article Author",dbArticle.getAuthor());
        Assert.assertEquals("Test Article Description",dbArticle.getDescription());
        Assert.assertEquals("Test Article UrlToImage",dbArticle.getUrlToImage());
        Assert.assertEquals("5 Jan",dbArticle.getPublishedAtString());
    }

    @Test
    public void checkDeleteArticle() {
        addArticle();
        Article dbArticle = db.getArticleByTitle("Test Article Title");
        db.deleteArticle(dbArticle);
        dbArticle = db.getArticleByTitle("Test Article Title");
        Assert.assertNull(dbArticle);
    }

    @Test
    public void checkDeleteNotExistingArticle() {
        Article dbArticle = createTestArticle();
        db.deleteArticle(dbArticle);
        dbArticle = db.getArticleByTitle("Test Article Title");
        Assert.assertNull(dbArticle);
    }

    @Test
    public void checkDeleteArticleByTitle() {
        addArticle();
        Article dbArticle = db.getArticleByTitle("Test Article Title");
        dbArticle.setDbId(-1);
        db.deleteArticle(dbArticle);
        dbArticle = db.getArticleByTitle("Test Article Title");
        Assert.assertNull(dbArticle);
    }

    @Test
    public void checkDeleteSource() {
        Source source = new Source("TesteSourceId","TestSourse");
        db.addSource(source);
        source = db.getSource("TesteSourceId");
        db.deleteSource(source);
        source = db.getSource("TesteSourceId");
        Assert.assertNull(source);
    }
}