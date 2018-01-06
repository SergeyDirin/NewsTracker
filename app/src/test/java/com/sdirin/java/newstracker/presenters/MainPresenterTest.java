package com.sdirin.java.newstracker.presenters;

import android.util.Log;

import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.data.network.mock.Requests;
import com.sdirin.java.newstracker.data.parse.NewsServiceParser;
import com.sdirin.java.newstracker.database.DatabaseHandler;
import com.sdirin.java.newstracker.view.MainScreen;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by SDirin on 06-Jan-18.
 */
public class MainPresenterTest {
    @Mock
    MainScreen screenMock;
    @Mock
    DatabaseHandler dbMock;
    @Mock
    Log logMock;

    @Captor
    ArgumentCaptor<NewsResponse> captor;

    @Before
    public void setUp() throws Exception {
        screenMock = mock(MainScreen.class);
        dbMock = mock(DatabaseHandler.class);
        logMock = mock(Log.class);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void loadFromDB() throws Exception {
        when(screenMock.getDb()).thenReturn(dbMock);
        NewsResponse newsResponse = NewsServiceParser.fromJson(Requests.ARTICLES_JSON);
        when(dbMock.getAllArticles()).thenReturn(newsResponse.getArticles());
        MainPresenter presenter = new MainPresenter(screenMock);

        presenter.onResume();

        verify(screenMock).setNewsResponse(captor.capture());

        Assert.assertEquals(2,captor.getValue().getArticles().size());
    }

}