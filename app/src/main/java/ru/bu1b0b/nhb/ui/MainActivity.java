package ru.bu1b0b.nhb.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import ru.bu1b0b.nhb.R;
import ru.bu1b0b.nhb.model.News;
import ru.bu1b0b.nhb.ui.base.BaseActivity;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private InterstitialAd interstitial;
    private AdView mAdView;

    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipe_container;
    private MainNewsAdapter newsAdapter;
    private RecyclerView recyclerView;

    private final String newsURL = "http://hural-buryatia.ru/news?t=0&p=%s&c=20&search=";

    private List<News> newsList;

    private boolean addToStart = false;
    private boolean isRefreshing = false;
    private int pageCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbarMain));

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(getAdRequest());
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-4899358131893161/2729545339");
        if (!interstitial.isLoaded()) {
            loadInterstitialAd();
        }

        swipe_container = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipe_container.setOnRefreshListener(this);

        newsList = new ArrayList<News>();
        newsAdapter = new MainNewsAdapter(this, newsList);
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(scrollListener);
        recyclerView.setAdapter(newsAdapter);

        showProgressDialog();
        new MainActivity.NewThread().execute();

    }

    public void startInterstitialAd(final Intent intent) {
        interstitial.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
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
//        AdRequest adRequest = new AdRequest.Builder().build();

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("88069E64671605BD435B14C02C54498A")
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        return adRequest;
    }

    private void requestNewInterstitial() {
        interstitial.loadAd(getAdRequest());
    }

    private String getURL(int pageCount) {
        return String.format(newsURL, pageCount);
    }

    @Override
    protected void onPause() {
        mAdView.pause();
        super.onPause();
        swipe_container.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdView.resume();
    }

    @Override
    protected void onDestroy() {
        mAdView.destroy();
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipe_container.setRefreshing(false);
                addToStart = true;
                isRefreshing = true;
                new MainActivity.NewThread().execute();

            }
        }, 3000);
    }

    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = linearLayoutManager.getChildCount();//смотрим сколько элементов на экране
            int totalItemCount = linearLayoutManager.getItemCount();//сколько всего элементов
            int firstVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();//какая позиция первого элемента

            if ((visibleItemCount + firstVisibleItems) >= totalItemCount) {
                isRefreshing = true;
                new MainActivity.NewThread().execute();
                pageCount++;
            }
        }
    };

    public class NewThread extends AsyncTask<String, Void, String> {
        Document doc;
        Element content;

        @Override
        protected String doInBackground(String... params) {
            try {
                if (isRefreshing) {
                    doc = Jsoup.connect(getURL(1)).get();
                } else {
                    doc = Jsoup.connect(getURL(pageCount)).get();
                }

                content = doc.getElementById("content");
                for (int i = 0; i < content.select("b > a[href*=/news?record_id=]").size(); i++) {
                    News n = new News();
                    n.setTitle(content.select("b > a[href*=/news?record_id=]").get(i).text());
                    n.setPublished(content.select("div[style=\"color:red\"]").get(i).text());
                    n.setLinks(content.select("b > a[href*=/news?record_id=]").get(i).attr("href"));
                    if (!isContained(n)) {
                        if (addToStart) {
                            newsList.add(i, n);
                        } else if (!addToStart) {
                            newsList.add(n);
                        }
                    }
                }
            } catch (Exception e) {
            }
            return null;
        }

        private boolean isContained(News n) {
            boolean b = false;
            for (News s : newsList) {
                if (s.getTitle().equals(n.getTitle())) {
                    b = true;
                }
            }
            return b;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String s) {
            hideProgressDialog();
            newsAdapter.notifyDataSetChanged();
            addToStart = false;
            isRefreshing = false;
        }
    }
}
