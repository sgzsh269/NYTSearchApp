package com.sagarnileshshah.nytsearchapp.models;

import com.sagarnileshshah.nytsearchapp.models.gson.Doc;
import com.sagarnileshshah.nytsearchapp.models.gson.JSONTopLevel;

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
    String thumbNailUrl;

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbNailUrl() {
        return thumbNailUrl;
    }

    public static Article fromJson(JSONObject jsonObject) {

        Article article = new Article();

        try {
            article.webUrl = jsonObject.getString("web_url");
            article.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimediaJSONArray = jsonObject.getJSONArray("multimedia");
            if (multimediaJSONArray.length() > 0) {
                JSONObject multimediaJSONObject = multimediaJSONArray.getJSONObject(0);
                article.thumbNailUrl = "http://www.nytimes.com/" + multimediaJSONObject.getString("url");
            } else {
                article.thumbNailUrl = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return article;
    }

    public static ArrayList<Article> fromJSONArray(JSONArray jsonArray) {

        ArrayList<Article> articleList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                articleList.add(Article.fromJson(jsonArray.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return articleList;
    }

    public static ArrayList<Article> fromGson(JSONTopLevel jsonTopLevel) {
        ArrayList<Doc> docs = (ArrayList) jsonTopLevel.getResponse().getDocs();
        ArrayList<Article> articleList = new ArrayList<>();
        for (Doc doc : docs) {
            Article article = new Article();
            article.webUrl = doc.getWebUrl();
            article.headline = doc.getHeadline().getMain();
            if (doc.getMultimedia().size() > 0) {
                article.thumbNailUrl = "http://www.nytimes.com/" + doc.getMultimedia().get(0).getUrl();
            } else {
                article.thumbNailUrl = "";
            }
            articleList.add(article);
        }
        return articleList;
    }
}
