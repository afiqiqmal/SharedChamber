package com.zeroone.conceal.model;

/**
 * @author : hafiq on 26/03/2017.
 */
@SuppressWarnings("unused")
public class CryptoFile {

    private int id;
    private String path;
    private String fileName;
    private String type;

    public void setId(int id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public String getType() {
        return type;
    }
}
