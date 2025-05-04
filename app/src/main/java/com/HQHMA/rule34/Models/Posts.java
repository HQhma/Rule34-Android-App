package com.HQHMA.rule34.Models;

import java.util.Arrays;

public class Posts {

    private long id;
    private String preview_url,file_url,sample_url,owner,file_name;
    private int comment_count;
    private String[] tags;
    private Boolean isExpanded;
    private Boolean highQualityLoaded;
    int height,width;



    public Posts(long id, String preview_url, String file_url, String sample_url, String file_name, String owner, int comment_count, String[] tags, int height, int width) {
        this.id = id;
        this.preview_url = preview_url;
        this.file_url = file_url;
        this.sample_url = sample_url;
        this.file_name = file_name;
        this.owner = owner;
        this.comment_count = comment_count;
        this.tags = tags;
        this.isExpanded = false;
        this.highQualityLoaded = false;
        this.height = height;
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Boolean getExpanded() {
        return isExpanded;
    }

    public void setExpanded(Boolean expanded) {
        isExpanded = expanded;
    }

    public Boolean getHighQualityLoaded() {
        return highQualityLoaded;
    }

    public void setHighQualityLoaded(Boolean highQualityLoaded) {
        this.highQualityLoaded = highQualityLoaded;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public String getSample_url() {
        return sample_url;
    }

    public void setSample_url(String sample_url) {
        this.sample_url = sample_url;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getComment_count() {
        return comment_count;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public String[] getTags() {
        return tags;
    }

    public String getTagsStr() {
        if (tags != null){
            String tagsStr = "";
            for (String tag:tags) {
                tagsStr += tag + " ";
            }
            return tagsStr;
        }
        return "";
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Posts{" +
                "preview_url='" + preview_url + '\'' +
                ", file_url='" + file_url + '\'' +
                ", tags=" + Arrays.toString(tags) +
                '}';
    }
}
