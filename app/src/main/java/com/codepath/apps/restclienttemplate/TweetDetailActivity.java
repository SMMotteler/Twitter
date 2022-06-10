package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.net.ParseException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetsAdapter;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.Headers;

public class TweetDetailActivity extends AppCompatActivity {
    Tweet tweet;
    ImageView ivProfileImage;
    ImageView ivTweetPhoto;
    TextView tvBody;
    TextView tvScreenName;
    TextView tvRelTime;
    TextView tvName;
    ImageButton ibFavorite;
    TextView tvFavoriteCount;
    ImageButton ibComment;
    TextView tvCommentCount;
    ImageButton ibRetweet;
    TextView tvRetweetCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tweet_detail);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvBody = findViewById(R.id.tvBody);
        tvName = findViewById(R.id.tvName);
        tvScreenName = findViewById(R.id.tvScreenName);
        ivTweetPhoto = findViewById(R.id.ivTweetPhoto);
        tvRelTime = findViewById(R.id.tvRelTime);
        ibFavorite = findViewById(R.id.ibFavorite);
        tvFavoriteCount = findViewById(R.id.tvFavoriteCount);
        ibComment = findViewById(R.id.ibComment);
        //tvCommentCount = findViewById(R.id.tvCommentCount);
        ibRetweet = findViewById(R.id.ibRetweet);
        tvRetweetCount = findViewById(R.id.tvRetweetCount);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        if (tweet.isFavorited){
            Drawable newImage = this.getDrawable(android.R.drawable.btn_star_big_on);
            ibFavorite.setImageDrawable(newImage);
        }
        else{
            Drawable newImage = this.getDrawable(android.R.drawable.btn_star_big_off);
            ibFavorite.setImageDrawable(newImage);

        }
        if (tweet.isRetweeted){
            Drawable newImage = this.getDrawable(android.R.drawable.button_onoff_indicator_on);
            ibRetweet.setImageDrawable(newImage);
        }
        else {
            Drawable newImage = this.getDrawable(android.R.drawable.button_onoff_indicator_off);
            ibRetweet.setImageDrawable(newImage);

        }

        if (tweet.body == "") {
            tvBody.setVisibility(View.GONE);
        } else {
            tvBody.setVisibility(View.VISIBLE);
            tvBody.setText(tweet.body);

        }
        tvName.setText("@"+tweet.user.screenName);
        tvScreenName.setText(tweet.user.name);

        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        try {
            long time = sf.parse(tweet.createdAt).getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm z ' · ' MM/dd/yyyy");
            tvRelTime.setText(formatter.format(time));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        Glide.with(this).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage);
        if (tweet.hasPhoto) {
            ivTweetPhoto.setVisibility(View.VISIBLE);
            Glide.with(this).load(tweet.imageURL).centerCrop().transform(new RoundedCorners(30)).into(ivTweetPhoto);
            Log.i("tweets adapter", "photo is here " + tweet.imageURL);
        } else {
            ivTweetPhoto.setVisibility(View.GONE);
            Log.i("tweets adapter", "photo is not here");

        }
        //ivTweetPhoto.setVisibility(View.VISIBLE);
        //Glide.with(context).load(tweet.imageURL).into(ivTweetPhoto);
        tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
        tvRetweetCount.setText(String.valueOf(tweet.retweetedCount));
        //tvCommentCount.setText(String.valueOf(tweet.commentCount));

        ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if not already Favorited
                if(!tweet.isFavorited){
                    // Hard: tell Twitter that I want to favorite this tweet
                    tweet.isFavorited = true;
                    TwitterApp.getTwitterClient(TweetDetailActivity.this).favorite(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("adapter", "Favorited");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("adapter", "oops");

                        }
                    });
                    // easy: change the drawable to btn_star_big_on
                    Drawable newImage = TweetDetailActivity.this.getDrawable(android.R.drawable.btn_star_big_on);
                    ibFavorite.setImageDrawable(newImage);
                    // med: increment the text inside tvFavoriteCount
                    tweet.favoriteCount++;
                    tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
                }
                else{
                    // else if already Favorited
                    // tell Twitter I want to unfavorite this
                    tweet.isFavorited = false;
                    TwitterApp.getTwitterClient(TweetDetailActivity.this).unfavorite(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("adapter", "unfavorited");

                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("adapter", "oops");

                        }
                    });
                    // change the drawable back to btn_star_big_off
                    Drawable newImage = TweetDetailActivity.this.getDrawable(android.R.drawable.btn_star_big_off);
                    ibFavorite.setImageDrawable(newImage);
                    // decrement the text inside tvFavoriteCount
                    tweet.favoriteCount--;
                    tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
                }
            }
        });

        ibComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // pop up a compose screen - not a brand new tweet though,
                // will have an extra attribute: "in_reply_to_status_id"
                Intent i = new Intent(TweetDetailActivity.this, ComposeActivity.class);
                i.putExtra("tweet_to_reply_to", Parcels.wrap(tweet));
                // context.startActivity(i);
                ((Activity) TweetDetailActivity.this).startActivityForResult(i, TimelineActivity.REQUEST_CODE);
                //tweet.commentCount++;
                //tvCommentCount.setText(String.valueOf(tweet.commentCount));

            }
        });

        ibRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if not already Favorited
                if(!tweet.isRetweeted){
                    // Hard: tell Twitter that I want to retweet this tweet
                    tweet.isRetweeted = true;
                    TwitterApp.getTwitterClient(TweetDetailActivity.this).retweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("adapter", "retweeted");
                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("adapter", "oops");

                        }
                    });
                    // easy: change the drawable to button_onoff_indicator_on
                    Drawable newImage = TweetDetailActivity.this.getDrawable(android.R.drawable.button_onoff_indicator_on);
                    ibRetweet.setImageDrawable(newImage);
                    // med: increment the text inside tvFavoriteCount
                    tweet.retweetedCount++;
                    tvRetweetCount.setText(String.valueOf(tweet.retweetedCount));
                }
                else{
                    // else if already Favorited
                    // tell Twitter I want to unfavorite this
                    tweet.isRetweeted = false;
                    TwitterApp.getTwitterClient(TweetDetailActivity.this).unretweet(tweet.id, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {
                            Log.i("adapter", "unretweeted");

                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                            Log.e("adapter", "oops");

                        }
                    });
                    // change the drawable back to button_onoff_indicator_off
                    Drawable newImage = TweetDetailActivity.this.getDrawable(android.R.drawable.button_onoff_indicator_off);
                    ibRetweet.setImageDrawable(newImage);
                    // decrement the text inside tvFavoriteCount
                    tweet.retweetedCount--;
                    tvRetweetCount.setText(String.valueOf(tweet.retweetedCount));
                }}
        });
    }

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();
            Log.d("Time calc", "Current time: " + formatter.format(now) + "; tweeted time: " + formatter.format(time));
            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException | java.text.ParseException e) {
            Log.i("making date", "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }
    }
