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
    public boolean retweeted;
    public User retweeter;
    public String imageURL;
    public String id;
    public int imgWidth, imgHeight;

    // empty constructor needed for the Parceler library
    public Tweet(){
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        JSONObject theJSON;
        Tweet tweet = new Tweet();
        theJSON = jsonObject;
        if (jsonObject.has("full_text")){
            tweet.body = theJSON.getString("full_text");
        }
        else{
            tweet.body = theJSON.getString("text");
        }
        tweet.createdAt = theJSON.getString("created_at");
        if (jsonObject.getBoolean("retweeted")){
            Log.d("retweet", jsonObject.getJSONObject("retweeted_status").toString());
            tweet.retweeted = true;
            tweet.retweeter = User.fromJson(jsonObject.getJSONObject("user"));
            theJSON = jsonObject.getJSONObject("retweeted_status");
            tweet.user = User.fromJson(jsonObject.getJSONObject("retweeted_status").getJSONObject("user"));
        }
        else{
            Log.d("retweet", "not a retweet");
            tweet.retweeted = false;
            tweet.retweeter = null;
            tweet.user = User.fromJson(theJSON.getJSONObject("user"));
        }
        tweet.id = theJSON.getString("id_str");
//        if (jsonObject.getJSONObject("entities").length() > 0){
//            if (jsonObject.getJSONObject("entities").has("media")){
//                if (jsonObject.getJSONObject("entities").getJSONArray("media").length() > 0){
//                    tweet.imageURL = jsonObject.getJSONObject("entities").getJSONArray("media")
//                            .getJSONObject(0).getString("media_url");
//                    tweet.hasPhoto = true;
//                }
//            }
//        }
        if (!theJSON.getJSONObject("entities").has("media")){
            Log.d("Tweet", "no image");
            tweet.imageURL = "none";
            tweet.hasPhoto = false;
        }
        else{
            Log.d("Tweet", "cool image: ");
            tweet.imageURL = theJSON.getJSONObject("entities").getJSONArray("media")
                            .getJSONObject(0).getString("media_url");
            tweet.hasPhoto = true;
            tweet.body = tweet.body.replace(theJSON.getJSONObject("entities").getJSONArray("media")
                    .getJSONObject(0).getString("url"),"");
            //tweet.imgHeight = jsonObject.getJSONObject("entities").getJSONArray("media")
            //        .getJSONObject(0).getJSONObject("sizes").getJSONObject("large").getInt("h");
            //tweet.imgWidth = jsonObject.getJSONObject("entities").getJSONArray("media")
            //        .getJSONObject(0).getJSONObject("sizes").getJSONObject("large").getInt("w");
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
