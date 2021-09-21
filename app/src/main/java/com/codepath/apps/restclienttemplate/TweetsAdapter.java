package com.codepath.apps.restclienttemplate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.TweetViewHolder> {

    List<Tweet> tweetList;

    public TweetsAdapter(List<Tweet> tweetList){
        this.tweetList = tweetList;
    }
    public static class TweetViewHolder extends RecyclerView.ViewHolder{
        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        public TweetViewHolder(View itemView){
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            Glide.with(itemView.getContext()).load(tweet.user.publicImageURL).into(ivProfileImage);
        }
    }

    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet, parent, false);
        return new TweetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TweetViewHolder holder, int position) {
        Tweet tweet = tweetList.get(position);

        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }

    //swiperRefreshLayout Methods

    public void clear(){
        tweetList.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<Tweet> tweetList){
        this.tweetList.addAll(tweetList);
        notifyDataSetChanged();
    }



}
