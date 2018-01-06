package com.sdirin.java.newstracker.presenters;

import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.view.MainScreen;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by SDirin on 06-Jan-18.
 */
public class MainPresenterTest {

    MainScreen screenMock;

    @Captor
    ArgumentCaptor<NewsResponse> captor;

    @Before
    public void setUp() throws Exception {
        screenMock = mock(MainScreen.class);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void loadFromDB() throws Exception {
        MainPresenter presenter = new MainPresenter(screenMock);


        presenter.onResume();

        verify(screenMock).setNewsResponse(captor.capture());

        Assert.assertEquals(10,captor.getValue().getArticles().size());
    }
}