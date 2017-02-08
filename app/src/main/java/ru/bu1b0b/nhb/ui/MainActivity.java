package ru.bu1b0b.nhb.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

import ru.bu1b0b.nhb.R;
import ru.bu1b0b.nhb.model.News;
import ru.bu1b0b.nhb.ui.base.BaseActivity;
import ru.bu1b0b.nhb.ui.news.NewsActivitity;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        NewsAsyncTask.NewsLoaderListener, MainNewsAdapter.NewsListener {

    private static final String InterstitialAd_KEY = "ca-app-pub-4899358131893161/2729545339";

    private InterstitialAd interstitial;
    private AdView mAdView;
    private SwipeRefreshLayout swipe_container;
    private MainNewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbarMain));

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(getAdRequest());
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId(InterstitialAd_KEY);
        if (!interstitial.isLoaded()) {
            loadInterstitialAd();
        }

        swipe_container = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);

        newsAdapter = new MainNewsAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadNews(page);
            }
        };
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(newsAdapter);

        showProgressDialog();
        loadNews(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdView.resume();
    }

    @Override
    protected void onPause() {
        mAdView.pause();
        super.onPause();
        hideLoadings();
    }

    @Override
    public void onStop() {
        super.onStop();
        NewsLoader.getInstance().cancelLoading();
    }

    @Override
    protected void onDestroy() {
        mAdView.destroy();
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        loadNews(1);
    }

    @Override
    public void onAlreadyLoading() {
        Log.i("TAGG", "onAlreadyLoading");
        hideLoadings();
    }

    @Override
    public void onNewsLoaded(int page, List<News> newsList) {
        Log.i("TAGG", "News loaded. Page: " + page + ", news: " + newsList);
        hideLoadings();
        boolean addToStart = page == 1;
        if (addToStart) {
            newsAdapter.addRecentNews(newsList);
        } else {
            newsAdapter.addOldNews(newsList);
        }
    }

    @Override
    public void onError(Throwable error) {
        Log.e("TAGG", "Load news error", error);
    }

    @Override
    public void onNewsClicked(News news) {
        Intent intent = new Intent(this, NewsActivitity.class);
        intent.putExtra("newsTitle", news.getTitle());
        intent.putExtra("newsLink", news.getLinks());
        intent.putExtra("newsPublished", news.getPublished());
        showNewsWithInterstitialAd(intent);
    }

    public void showNewsWithInterstitialAd(final Intent intent) {
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                loadInterstitialAd();
                startActivity(intent);
            }
        });
        if (interstitial.isLoaded()) {
            interstitial.show();
        } else {
            startActivity(intent);
        }
    }

    private void loadInterstitialAd() {
        interstitial.loadAd(getAdRequest());
    }

    private AdRequest getAdRequest() {
        return new AdRequest.Builder()
                .addTestDevice("88069E64671605BD435B14C02C54498A")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
    }


    private void loadNews(int page) {
        NewsLoader.getInstance().loadNews(page, this);
    }

    private void hideLoadings() {
        swipe_container.setRefreshing(false);
        hideProgressDialog();
    }
}
