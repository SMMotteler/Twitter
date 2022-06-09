package com.codepath.apps.restclienttemplate.models;

import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {

    public String body;
    public String createdAt;
    public User user;
    public boolean hasPhoto;
    public String imageURL;
    public int imgWidth, imgHeight;

    // empty constructor needed for the Parceler library
    public Tweet(){
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
//        if (jsonObject.getJSONObject("entities").length() > 0){
//            if (jsonObject.getJSONObject("entities").has("media")){
//                if (jsonObject.getJSONObject("entities").getJSONArray("media").length() > 0){
//                    tweet.imageURL = jsonObject.getJSONObject("entities").getJSONArray("media")
//                            .getJSONObject(0).getString("media_url");
//                    tweet.hasPhoto = true;
//                }
//            }
//        }
        if (!jsonObject.getJSONObject("entities").has("media")){
            Log.d("Tweet", "no image");
            tweet.imageURL = "none";
            tweet.hasPhoto = false;
        }
        else{
            Log.d("Tweet", "cool image: ");
            tweet.imageURL = jsonObject.getJSONObject("entities").getJSONArray("media")
                            .getJSONObject(0).getString("media_url");
            tweet.hasPhoto = true;
            tweet.body = tweet.body.replace(jsonObject.getJSONObject("entities").getJSONArray("media")
                    .getJSONObject(0).getString("url"),"");
            tweet.imgHeight = jsonObject.getJSONObject("entities").getJSONArray("media")
                    .getJSONObject(0).getJSONObject("sizes").getJSONObject("large").getInt("h");
            tweet.imgWidth = jsonObject.getJSONObject("entities").getJSONArray("media")
                    .getJSONObject(0).getJSONObject("sizes").getJSONObject("large").getInt("w");
        }


        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
