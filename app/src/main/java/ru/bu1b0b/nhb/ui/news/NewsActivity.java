package ru.bu1b0b.nhb.ui.news;


import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;

import ru.bu1b0b.nhb.R;
import ru.bu1b0b.nhb.ui.base.BaseActivity;
import ru.yandex.speechkit.SpeechKit;
import ru.yandex.speechkit.Synthesis;
import ru.yandex.speechkit.Vocalizer;
import ru.yandex.speechkit.VocalizerListener;

/**
 * Created by bu1b0b on 01.02.2017.
 */

public class NewsActivity extends BaseActivity implements VocalizerListener {

    private static final String API_KEY = "31628fe9-e8d3-4adf-b729-ddfde5518fbf";

    private String newsURL = "http://hural-buryatia.ru";

    private ImageView newsImage;
    private Bitmap bitmap;
    private WebView webView;
    private FloatingActionButton fab;
    private Vocalizer vocalizer;
    private String currentNewsTitle;
    private String currentNewsPublished;
    private String newsText = "";
    private String textToSpeech = "";
    private boolean isSpeech = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        SpeechKit.getInstance().configure(getApplicationContext(), API_KEY);

        TextView news_title = (TextView) findViewById(R.id.news_title);
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setBackgroundColor(Color.parseColor("#EEEEEE"));
        webView.setScrollContainer(false);

        news_title.setText(getIntent().getStringExtra("newsTitle"));
        newsURL += getIntent().getStringExtra("newsLink");

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getIntent().getStringExtra("newsPublished"));
        newsImage = (ImageView) findViewById(R.id.news_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentNewsTitle = getIntent().getStringExtra("newsTitle");
        currentNewsPublished = getIntent().getStringExtra("newsPublished");
        new NewsDetailThread().execute();

        fab = (FloatingActionButton) findViewById(R.id.speech_news);
        fab.setAlpha(0.70f);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        resetVocalizer();
        isSpeech = false;
    }

    //==========================================================

    @Override
    public void onSynthesisBegin(Vocalizer vocalizer) {
        // nothing
    }

    @Override
    public void onSynthesisDone(Vocalizer vocalizer, Synthesis synthesis) {
        // nothing
    }

    @Override
    public void onPlayingBegin(Vocalizer vocalizer) {
        // nothing
    }

    @Override
    public void onPlayingDone(Vocalizer vocalizer) {
        resetVocalizer();
        isSpeech = false;
    }

    @Override
    public void onVocalizerError(Vocalizer vocalizer, ru.yandex.speechkit.Error error) {
        Toast.makeText(getApplicationContext(), "Прослушивание прервано", Toast.LENGTH_SHORT).show();
        resetVocalizer();
        isSpeech = false;
    }

    private void resetVocalizer() {
        if (vocalizer != null) {
            vocalizer.cancel();
            vocalizer = null;
        }
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#607D8B")));
        fab.setAlpha(0.70f);
    }

    private void initFabListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSpeech) {
                    Toast.makeText(getApplicationContext(), "Прослушивание начато", Toast.LENGTH_SHORT).show();
                    isSpeech = true;
                    resetVocalizer();
                    vocalizer = Vocalizer.createVocalizer(Vocalizer.Language.RUSSIAN, textToSpeech, true, Vocalizer.Voice.ZAHAR);
                    vocalizer.setListener(NewsActivity.this);
                    vocalizer.start();
                    fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                    fab.setAlpha(1f);
                } else {
                    Toast.makeText(getApplicationContext(), "Прослушивание закончено", Toast.LENGTH_SHORT).show();
                    isSpeech = false;
                    resetVocalizer();
                }
            }
        });
    }

    //==========================================================

    public class NewsDetailThread extends AsyncTask<String, Void, String> {
        Document doc;
        Element content;

        @Override
        protected String doInBackground(String... params) {
            try {
                doc = Jsoup.connect(newsURL).get();
                content = doc.getElementById("content");

                URL url = new URL(content.select("img").first().absUrl("src"));
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                content.select("h3").remove();
                content.select("div[style=\"text-align:justify;\"] > div").first().remove();
                content.select("img").first().remove();
                content.select("br").remove();

                newsText += content.select("div[style=\"text-align:justify;\"]");
                newsText = newsText.replaceAll(currentNewsTitle, "");
                newsText = newsText.replaceAll(currentNewsPublished, "");

                textToSpeech = content.select("div[style=\"text-align:justify;\"]").text();
                textToSpeech = textToSpeech.replaceAll(currentNewsTitle, "");
                textToSpeech = textToSpeech.replaceAll(currentNewsPublished, "");

            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "An error occurred while loading news detail", e);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            hideProgressDialog();
            newsImage.setImageBitmap(bitmap);

            String html = "<html><body style=\"color: #616161; background-color:#EEEEEE \">" + newsText + "</body></html>";
            String mime = "text/html; charset=utf-8";
            String encoding = "UTF-8";

            textToSpeech = currentNewsTitle + textToSpeech;
            initFabListener();

            webView.loadData(html, mime, encoding);
        }
    }
}

