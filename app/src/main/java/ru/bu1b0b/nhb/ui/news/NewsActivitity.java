package ru.bu1b0b.nhb.ui.news;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;

import ru.bu1b0b.nhb.R;
import ru.bu1b0b.nhb.ui.base.BaseActivity;

/**
 * Created by bu1b0b on 01.02.2017.
 */

public class NewsActivitity extends BaseActivity {

    private String newsURL = "http://hural-buryatia.ru";

    private CollapsingToolbarLayout collapsing_toolbar;
    private ImageView news_image;
    private Toolbar toolbar;

    private TextView news_title;
    private WebView web_view;

    private Bitmap bmp;

    private String currentNewsTitle;
    private String currentNewsPublished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        news_title = (TextView) findViewById(R.id.news_title);
        web_view = (WebView) findViewById(R.id.web_view);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.setBackgroundColor(Color.parseColor("#EEEEEE"));
        web_view.setScrollContainer(false);

        news_title.setText(getIntent().getStringExtra("newsTitle"));
        newsURL += getIntent().getStringExtra("newsLink");

        collapsing_toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsing_toolbar.setTitle(getIntent().getStringExtra("newsPublished"));
        news_image = (ImageView) findViewById(R.id.news_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentNewsTitle = getIntent().getStringExtra("newsTitle");
        currentNewsPublished = getIntent().getStringExtra("newsPublished");
        new NewsActivitity.NewThread().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class NewThread extends AsyncTask<String, Void, String> {
        Document doc;
        Element content;

        String ss = "";

        @Override
        protected String doInBackground(String... params) {
            try {
                doc = Jsoup.connect(newsURL).get();
                content = doc.getElementById("content");

                URL url = new URL(content.select("img").first().absUrl("src"));
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                content.select("h3").remove();
                content.select("div[style=\"text-align:justify;\"] > div").first().remove();
                content.select("img").first().remove();
                content.select("br").remove();

                ss += content.select("div[style=\"text-align:justify;\"]");
                ss = ss.replaceAll(currentNewsTitle, "");
                ss = ss.replaceAll(currentNewsPublished, "");

            } catch (Exception e) {}
            return null;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            hideProgressDialog();
            news_image.setImageBitmap(bmp);

            String html = "<html><body style=\"color: #616161; background-color:#EEEEEE \">" + ss + "</body></html>";
            String mime = "text/html; charset=utf-8";
            String encoding = "UTF-8";

            web_view.loadData(html, mime, encoding);
        }
    }
}
