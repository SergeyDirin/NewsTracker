package com.sdirin.java.newstracker.data.model.parse;

import com.sdirin.java.newstracker.data.model.Article;
import com.sdirin.java.newstracker.data.model.NewsResponse;
import com.sdirin.java.newstracker.data.model.Source;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SDirin on 02-Jan-18.
 */

public class NewsParser {

    public static NewsResponse fromJson(String json) throws ParseException {

        NewsResponse newsResponse = new NewsResponse();

        try {
            JSONObject root = new JSONObject(json);
            newsResponse.setStatus(root.getString("status"));
            if (newsResponse.getStatus().equals("error")){
                //error
                newsResponse.setCode(root.getString("code"));
                newsResponse.setMessage(root.getString("message"));
                return newsResponse;
            } else {
                if (! newsResponse.getStatus().equals("ok")){
                    throw new ParseException("Unable to parse json", 0);
                }
                //articles
                JSONArray articles = root.getJSONArray("articles");

                List<Article> articlesList = new ArrayList<>();
                for (int i = 0; i < articles.length(); i++){
                    JSONObject jsonArticle = articles.getJSONObject(i);
                    Article articleObj = new Article();
                    articleObj.setAuthor(jsonArticle.getString("author"));
                    articleObj.setTitle(jsonArticle.getString("title"));
                    articleObj.setDescription(jsonArticle.getString("description"));
                    articleObj.setUrl(jsonArticle.getString("url"));
                    articleObj.setUrlToImage(jsonArticle.getString("urlToImage"));
                    articleObj.setPublishedAtString(jsonArticle.getString("publishedAt"));

                    JSONObject jsonSource = jsonArticle.getJSONObject("source");
                    String sourceId = jsonSource.isNull("id") ? "Unknown" : jsonSource.getString("id");
                    String sourceName = jsonSource.isNull("name") ? "Unknown" : jsonSource.getString("name");
                    Source source = new Source();
                    source.setId(sourceId);
                    source.setName(sourceName);
                    articleObj.setSource(source);
                    articlesList.add(articleObj);
                }
                newsResponse.setArticles(articlesList);
            }




        } catch (JSONException e) {
            e.printStackTrace();
            throw new ParseException("Unable to parse json", 0);
        }

        return newsResponse;
    }

}
