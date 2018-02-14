package com.sdirin.java.newstracker.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.sdirin.java.newstracker.data.model.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by User on 07.02.2018.
 */

public class SelectedSources {

    public static final String PREF = "com.sdirin.java.newstracker";
    private static final String PREF_SELECTED_SOURCES = "com.sdirin.java.newstracker.selectedsources";

    Context context;
    SharedPreferences prefs;
    public List<String> selected;

    public SelectedSources(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(
                PREF, Context.MODE_PRIVATE);
        loadSelected();
    }

    private void loadSelected(){
        String sources = getSelectedSources();
        if (sources.length() == 0) {
            selected = new ArrayList<>();
        } else {
            selected = Arrays.asList(sources.split(","));
        }
    }

    public void setSource(Source source){
        if (selected.contains(source.getId())){
            return;
        }
        String sources = getSelectedSources();
        if (sources.length() == 0) {
            saveSoures(source.getId());
        } else {
            saveSoures(sources+","+source.getId());
        }
        loadSelected();
    }

    public void removeSource(Source source){
        String sources = getSelectedSources();
        if (sources.length() == 0) {
            return;
        } else {
            List<String> split = Arrays.asList(sources.split(","));
            StringBuilder newSources = new StringBuilder();
            for (int i = 0; i < split.size(); i++) {
                if (split.get(i).equals(source.getId())){
                    continue;
                }
                newSources.append(split.get(i)+",");
            }
            newSources = newSources.deleteCharAt(newSources.length()-1);
            saveSoures(newSources.toString());
        }
        loadSelected();
    }

    void saveSoures(String s){
        prefs.edit().putString(PREF_SELECTED_SOURCES, s).apply();
    }

    public String getSelectedSources(){
        return prefs.getString(PREF_SELECTED_SOURCES, "");
    }

    public boolean has(String source) {
        return selected.contains(source);
    }
}
