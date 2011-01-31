package org.far.twoduiproject;

public class PCategory{
    private int id;
    private String path;
    
    public PCategory(int id, String path){
        this.setId(id);
        this.setPath(path);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
