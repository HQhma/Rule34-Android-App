package com.HQHMA.rule34.Utilities;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.HQHMA.rule34.R;

public class UrlUtilities {

    public static String getUrl(String searchTags, Context context){

        int limit = Utilities.postLimit;

        String start_url = "https://api.rule34.xxx/index.php?page=dapi&s=post&q=index&json=1";

        if (limit > 0)
            start_url += "&limit=" + limit;

        // how to sort tags=sort:score:desc
        // sort:score:desc

        SharedPreferences sharedPreferences = context.getSharedPreferences("Rule34",MODE_PRIVATE);
        String[] spinnerItemCodes = context.getResources().getStringArray(R.array.sort_option_code);
        int sortSpinnerItem_Index = sharedPreferences.getInt("sortSpinnerItem_Index",0);
        start_url += "&tags=" + spinnerItemCodes[sortSpinnerItem_Index];

        if (searchTags != null)
            start_url += " " + searchTags;

        Log.i("UrlUtilities", "getUrl: " + start_url);
        return start_url;
    }

    public static String getTagsUrlForSearchSuggest(String searchTags){

        //name_pattern=%nude%
        //order=count
        //limit=5
        //direction=desc

        String start_url = "https://api.rule34.xxx/index.php?page=dapi&s=tag&q=index";

        start_url += "&order=count&direction=desc&limit=5";

        if (searchTags != null)
            start_url += "&name_pattern=%" + searchTags + "%";

        Log.i("UrlUtilities", "getTagsUrlForSearchSuggest: " + start_url);
        return start_url;
    }
    public static String getTagUrl(String searchTags){

        //name_pattern=%nude%
        //order=count
        //limit=5
        //direction=desc

        String start_url = "https://api.rule34.xxx/index.php?page=dapi&s=tag&q=index";

        start_url += "&order=count&direction=desc&name="+searchTags;

        Log.i("UrlUtilities", "getTagUrl: " + start_url);
        return start_url;
    }

}
