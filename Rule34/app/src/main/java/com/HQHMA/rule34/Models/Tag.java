package com.HQHMA.rule34.Models;

import com.HQHMA.rule34.R;

public class Tag {

    private String value;
    private Long id,count;
    private int type;

    final private String[] typeText = new String[]{"General","Artist","Other","Copyright","Character","Meta"};
    final private int[] intText = new int[]{
            R.color.tag_General,
            R.color.tag_Artist,
            R.color.tag_Other,
            R.color.tag_Copyright,
            R.color.tag_Character,
            R.color.tag_Meta
    };


    public Tag(String value, Long id, Long count,int type) {
        this.value = value;
        this.id = id;
        this.count = count;
        this.type = type;
    }

    public Tag(String value, Long id) {
        this.value = value;
        this.id = id;
        this.count = 0L;
        this.type = 0;
    }

    public Tag(String value) {
        this.value = value;
        this.id = 0L;
        this.count = 0L;
        this.type = 0;
    }

    @Override
    public String toString() {
        return "\nTag{" +
                "value='" + value + '\'' +
                ", id=" + id +
                '}';
    }

    public String getValue() {
        if (value.isEmpty())
            return "error_value";
        return value;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getType(){
        return typeText[type];
    }
    public int getTypeIndex(){
        return type;
    }
    public int getTypeColor(){
        return intText[type];
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCount() {
        return count;
    }
}
