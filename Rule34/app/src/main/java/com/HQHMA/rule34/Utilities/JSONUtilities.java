package com.HQHMA.rule34.Utilities;

import android.util.Log;

import com.HQHMA.rule34.Models.Posts;
import com.HQHMA.rule34.Models.Tag;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.util.ArrayList;
import java.util.List;

public class JSONUtilities {

    public static void convertJSONToPosts(String JSON,ArrayList<Posts> posts) {
        try {
            JSONArray mainJSA = new JSONArray(JSON);

            for (int i = 0; i < mainJSA.length(); i++) {
                JSONObject subObject = mainJSA.getJSONObject(i);

                long id;
                int comment_count;
                String preview_url,file_url,sample_url,file_name,owner;
                String[] tags;
                Posts newPost;
                int width,height;
                try {
                    id = Long.parseLong(subObject.getString("id"));
                    preview_url = subObject.getString("preview_url");
                    file_url = subObject.getString("file_url");
                    sample_url = subObject.getString("sample_url");
                    file_name = subObject.getString("image");
                    owner = subObject.getString("owner");
                    comment_count = Integer.parseInt(subObject.getString("comment_count"));
                    tags = subObject.getString("tags").split(" ");
                    width = Integer.parseInt(subObject.getString("width"));
                    height = Integer.parseInt(subObject.getString("height"));
                    newPost = new Posts(id,preview_url,file_url,sample_url,file_name,owner,comment_count,tags,height,width);
                    posts.add(newPost);
                }catch (Exception e){
                    Log.i("response", "error item");
                }

            }

        } catch (Exception e) {
            Log.e("Error Exception", e.toString());
        }

        Log.i("JSONUtilities", "convertJSONToPosts: Posts Count: " + posts.size());
    }

    public static void convertXMLToTags(String xmlString, List<Tag> tag) {
        try {
            JSONObject jsonObject = XML.toJSONObject(xmlString);
            /*String jsonString = jsonObject.toString();
            Log.i("response", "convertXMLToTags: " + jsonString);*/
            JSONObject tags = jsonObject.getJSONObject("tags");
            JSONArray mainJSA = tags.getJSONArray("tag");

            for (int i = 0; i < mainJSA.length(); i++) {
                JSONObject subObject = mainJSA.getJSONObject(i);

                long id,count;
                int type;
                String value;
                Tag newTag;
                try {
                    id = Long.parseLong(subObject.getString("id"));
                    value = subObject.getString("name");
                    count = Long.parseLong(subObject.getString("count"));
                    type = Integer.parseInt(subObject.getString("type"));
                    newTag = new Tag(value,id,count,type);
                    tag.add(newTag);
                }catch (Exception e){
                    Log.i("response", "error item");
                }

            }

        } catch (Exception e) {
            Log.e("Error Exception", e.toString());
        }


    }
    public static Tag convertXMLToTag(String xmlString) {
        Tag newTag = new Tag("error");
        try {
            JSONObject jsonObject = XML.toJSONObject(xmlString);
            /*String jsonString = jsonObject.toString();
            Log.i("response", "convertXMLToTags: " + jsonString);*/
            JSONObject tags = jsonObject.getJSONObject("tags");
            JSONObject mainJSA = tags.getJSONObject("tag");

            long id,count;
            int type;
            String value;
            try {
                id = Long.parseLong(mainJSA.getString("id"));
                value = mainJSA.getString("name");
                count = Long.parseLong(mainJSA.getString("count"));
                type = Integer.parseInt(mainJSA.getString("type"));
                newTag = new Tag(value,id,count,type);
            }catch (Exception e){
                Log.i("response", "error item");
            }

        } catch (Exception e) {
            Log.e("Error Exception", e.toString());
        }

        return newTag;
    }
}
