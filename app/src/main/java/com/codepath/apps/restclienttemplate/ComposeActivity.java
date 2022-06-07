package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ComposeActivity extends AppCompatActivity {

    EditText tvTweetText;
    Button btnTweet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        btnTweet = findViewById(R.id.btnTweet);
        tvTweetText = findViewById(R.id.tvTweetText);
    }

    public void onTweetButton(View view){
        String tweetText = tvTweetText.getText().toString();
        if (tweetText.length() < 1) {
            Toast.makeText(this, "Tweet is empty", Toast.LENGTH_SHORT).show();
        }
        else if (tweetText.length() > 280) {
            Toast.makeText(this, "Tweet is over by "+(tweetText.length()-280)+" characters.", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, tweetText, Toast.LENGTH_SHORT).show();
        }
    }
}