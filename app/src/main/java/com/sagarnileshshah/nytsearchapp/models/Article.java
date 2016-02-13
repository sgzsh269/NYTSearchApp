package com.sagarnileshshah.nytsearchapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by sshah on 2/10/16.
 */

@Parcel
public class Article {

    String webUrl;
    String headline;
    String thumbNail;

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNail() {
        return thumbNail;
    }

    public static Article fromJson(JSONObject jsonObject){

        Article article = new Article();

        try {
            article.webUrl = jsonObject.getString("web_url");
            article.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimediaJSONArray = jsonObject.getJSONArray("multimedia");
            if(multimediaJSONArray.length() > 0){
                JSONObject multimediaJSONObject = multimediaJSONArray.getJSONObject(0);
                article.thumbNail = "http://www.nytimes.com/" + multimediaJSONObject.getString("url");
            } else {
                article.thumbNail = "";
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        return article;
    }

    public static ArrayList<Article> fromJSONArray(JSONArray jsonArray){

        ArrayList<Article> articleList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++){
            try {
                articleList.add(Article.fromJson(jsonArray.getJSONObject(i)));
            } catch (JSONException e){
                e.printStackTrace();
            }
        }

        return articleList;
    }
}
