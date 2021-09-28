package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    TwitterClient client;



    private final String TAG = "TimelineActivity";
    TweetsAdapter tweetsAdapter;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    SwipeRefreshLayout swContainer;
    List<Tweet> tweetList = new ArrayList<>();
    private final int REQUEST_CODE = 20;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        //set up recycler view
        recyclerView = findViewById(R.id.rvTweets);
        tweetsAdapter = new TweetsAdapter(tweetList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tweetsAdapter);

        // Pagination scrollListener
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager){
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoad more");
                loadMore();
            }
        };
        recyclerView.addOnScrollListener(scrollListener);


        //setting swiperRefreshLayout
        swContainer = findViewById(R.id.swContainer);
        swContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "fetching data for refresh");
                pupolateHomeTineline();
            }
        });

        swContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        client = TwitterApp.getRestClient(this);
        pupolateHomeTineline();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.compose){
            Intent i = new Intent(this, ComposeActivity.class);
            startActivityForResult(i, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(REQUEST_CODE == requestCode && resultCode == RESULT_OK){
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweetList.add(0, tweet);
            tweetsAdapter.notifyItemInserted(0);
            recyclerView.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadMore() {
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "successful Next page population");
                JSONArray jsonArray = json.jsonArray;
                try {
                    List<Tweet> newTweets = Tweet.fromJsonArray(jsonArray);
//                    tweetsAdapter.clear();
                    tweetsAdapter.addAll(newTweets);
                }catch (JSONException e){
                    Log.e(TAG, "error fetching data", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "failure Next page population");
            }
        }, tweetList.get(tweetList.size() - 1).id);
    }

    private void pupolateHomeTineline() {
        client.getIHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "successful");
                JSONArray jsonArray = json.jsonArray;

                try {
                    tweetsAdapter.clear();
                    tweetsAdapter.addAll(Tweet.fromJsonArray(jsonArray));
                    swContainer.setRefreshing(false);

//                    tweetsAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Something went wrong getting tweets ", e);
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "failure", throwable);
            }
        });
    }



}