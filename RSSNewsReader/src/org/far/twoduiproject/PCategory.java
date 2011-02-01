package org.far.twoduiproject;

public class PCategory{
    private int id;
    private String path;
    private String encoding;
    
    public PCategory(int id, String path, String encoding){
        this.id = id;
        this.path = path;
        this.encoding = encoding;
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

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }
}
