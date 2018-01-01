package com.sdirin.java.newstracker.data.model;

/**
 * Created by SDirin on 01-Jan-18.
 */

public class Source {

    private String id;
    private String name;

    public Source(String id, String name) {

        this.id = id;
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {

        return id;
    }

    public String getName() {
        return name;
    }
}
