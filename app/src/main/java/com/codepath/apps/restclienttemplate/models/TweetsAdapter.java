package com.codepath.apps.restclienttemplate.models;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.net.ParseException;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TimelineActivity;
import com.codepath.apps.restclienttemplate.TweetDetailActivity;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    Context context;
    List<Tweet> tweets;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the data at position
        Tweet tweet = tweets.get(position);

        // Bind the tweet with view holder
        holder.bind(tweet);
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Define a viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfileImage;
        ImageView ivTweetPhoto;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvRelTime;
        ImageButton ibFavorite;
        TextView tvFavoriteCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivTweetPhoto = itemView.findViewById(R.id.ivTweetPhoto);
            tvRelTime = itemView.findViewById(R.id.tvRelTime);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
            tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);

            itemView.setOnClickListener(this);
        }

        public void bind(Tweet tweet) {
            if (tweet.body == "") {
                tvBody.setVisibility(View.GONE);
            } else {
                tvBody.setVisibility(View.VISIBLE);
                tvBody.setText(tweet.body);

            }
            tvScreenName.setText(tweet.user.screenName);
            tvRelTime.setText(getRelativeTimeAgo(tweet.createdAt));
            Glide.with(context).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage);
            if (tweet.hasPhoto) {
                ivTweetPhoto.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.imageURL).centerCrop().transform(new RoundedCorners(30)).into(ivTweetPhoto);
                Log.i("tweets adapter", "photo is here " + tweet.imageURL);
            } else {
                ivTweetPhoto.setVisibility(View.GONE);
                Log.i("tweets adapter", "photo is not here");

            }
            //ivTweetPhoto.setVisibility(View.VISIBLE);
            //Glide.with(context).load(tweet.imageURL).into(ivTweetPhoto);
            tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));

            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // if not already Favorited
                    if(!tweet.isFavorited){
                        // Hard: tell Twitter that I want to favorite this tweet
                        tweet.isFavorited = true;
                        TwitterApp.getTwitterClient(context).favorite(tweet.id, new JsonHttpResponseHandler() {
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
                        Drawable newImage = context.getDrawable(android.R.drawable.btn_star_big_on);
                        ibFavorite.setImageDrawable(newImage);
                        // med: increment the text inside tvFavoriteCount
                        tweet.favoriteCount++;
                        tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
                    }
                    else{
                    // else if already Favorited
                        // tell Twitter I want to unfavorite this
                        tweet.isFavorited = false;
                        TwitterApp.getTwitterClient(context).unfavorite(tweet.id, new JsonHttpResponseHandler() {
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
                        Drawable newImage = context.getDrawable(android.R.drawable.btn_star_big_off);
                        ibFavorite.setImageDrawable(newImage);
                        // decrement the text inside tvFavoriteCount
                        tweet.favoriteCount--;
                        tvFavoriteCount.setText(String.valueOf(tweet.favoriteCount));
                }}
            });
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                Intent showDetails = new Intent(context, TweetDetailActivity.class);
                showDetails.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(showDetails);

            }
        }

        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            try {
                long time = sf.parse(rawJsonDate).getTime();
                // the time of my system is off, so I adjusted it so that it displays correctly
                long now = System.currentTimeMillis() + 14 * HOUR_MILLIS + 42 * MINUTE_MILLIS;
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
}


