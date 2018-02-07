package com.sdirin.java.newstracker.data.model.parse;

import com.sdirin.java.newstracker.data.model.Source;
import com.sdirin.java.newstracker.data.model.SourcesResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 07.02.2018.
 */

public class SourcesParser {


    public static SourcesResponse fromJson(String json) throws ParseException {

        SourcesResponse sourcesResponse = new SourcesResponse();

        try {
            JSONObject root = new JSONObject(json);
            sourcesResponse.setStatus(root.getString("status"));
            if (sourcesResponse.getStatus().equals("error")){
                //error
                sourcesResponse.setCode(root.getString("code"));
                sourcesResponse.setMessage(root.getString("message"));
                return sourcesResponse;
            } else {
                if (! sourcesResponse.getStatus().equals("ok")){
                    throw new ParseException("Unable to parse json", 0);
                }
                //sources
                JSONArray sources = root.getJSONArray("sources");

                List<Source> sourceList = new ArrayList<>();
                for (int i = 0; i < sources.length(); i++){
                    JSONObject jsonSource = sources.getJSONObject(i);
                    Source sourceObj = new Source();
                    sourceObj.setId(jsonSource.getString("id"));
                    sourceObj.setName(jsonSource.getString("name"));
                    sourceObj.setDescription(jsonSource.getString("description"));
                    sourceObj.setUrl(jsonSource.getString("url"));
                    sourceObj.setCategory(jsonSource.getString("category"));
                    sourceObj.setLanguage(jsonSource.getString("language"));
                    sourceObj.setCountry(jsonSource.getString("country"));

                    sourceList.add(sourceObj);
                }
                sourcesResponse.setSources(sourceList);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            throw new ParseException("Unable to parse json", 0);
        }

        return sourcesResponse;
    }
}
