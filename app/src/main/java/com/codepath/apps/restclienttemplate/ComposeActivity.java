package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";

    EditText tvTweetText;
    Button btnTweet;

    TwitterClient client;
    JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Headers headers, JsonHttpResponseHandler.JSON
        json) {
            Log.i(TAG, "onSuccess to publish tweet");
            try {
                Tweet tweet = Tweet.fromJson((json.jsonObject));
                Log.i(TAG, "Published tweet says: "+tweet.body);
                Intent i = new Intent();
                i.putExtra("tweet", Parcels.wrap(tweet));
                // set result code and bundle data for response
                setResult(RESULT_OK, i);
                // close the activity and pass data to the parent
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            Log.e(TAG, "onFailure to reply to tweet", throwable);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getTwitterClient(this);
        btnTweet = findViewById(R.id.btnTweet);
        tvTweetText = findViewById(R.id.tvTweetText);
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetText = tvTweetText.getText().toString();
                if (tweetText.length() < 1) {
                    Toast.makeText(ComposeActivity.this, "Tweet is empty", Toast.LENGTH_SHORT).show();
                }
                else if (tweetText.length() > 280) {
                    Toast.makeText(ComposeActivity.this, "Tweet is over by "+(tweetText.length()-280)+" characters.", Toast.LENGTH_SHORT).show();
                }
                else{
                    // Toast.makeText(ComposeActivity.this, tweetText, Toast.LENGTH_SHORT).show();

                    if(getIntent().hasExtra("tweet_to_reply_to")){
                        Tweet tweet = Parcels.unwrap(getIntent().getParcelableExtra("tweet_to_reply_to"));
                        client.replyToTweet(tweet.id,"@"+tweet.user.screenName+" "+tweetText, handler);
                    }

                    else{
                    client.publishTweet(tweetText, handler);
                    }
                }
            }
        });
    }
}