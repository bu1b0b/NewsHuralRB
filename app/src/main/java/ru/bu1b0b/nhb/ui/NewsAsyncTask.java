package ru.bu1b0b.nhb.ui;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import ru.bu1b0b.nhb.model.News;

class NewsAsyncTask extends AsyncTask<String, List<News>, List<News>> {

    private static final String NEWS_URL = "http://hural-buryatia.ru/news?t=0&p=%s&c=20&search=";
    private int page;
    private NewsLoaderListener listener;
    private Throwable error;

    NewsAsyncTask(int page, NewsLoaderListener listener) {
        this.page = page;
        this.listener = listener;
    }

    @Override
    protected List<News> doInBackground(String... params) {
        Log.i("TAGG", "Load news page " + page);
        try {
            List<News> newsList = new ArrayList<>();
            Document doc = Jsoup.connect(getURL(page)).get();
            if (isCancelled()) {
                throw new IllegalStateException("Task was cancelled");
            }
            Element content = doc.getElementById("content");
            for (int i = 0; i < content.select("b > a[href*=/news?record_id=]").size(); i++) {
                News news = new News();
                Elements linkElements = content.select("b > a[href*=/news?record_id=]");
                Elements publishedElements = content.select("div[style=\"color:red\"]");
                news.setTitle(linkElements.get(i).text());
                news.setPublished(publishedElements.get(i).text());
                news.setLinks(linkElements.get(i).attr("href"));
                newsList.add(news);
            }
            return newsList;
        } catch (Exception e) {
            error = e;
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        // nothing
    }

    @Override
    protected void onPostExecute(List<News> news) {
        if (news == null) {
            listener.onError(error);
        } else {
            listener.onNewsLoaded(page, news);
        }
    }

    private String getURL(int pageCount) {
        return String.format(NEWS_URL, pageCount);
    }

    public interface NewsLoaderListener {

        void onAlreadyLoading();

        void onNewsLoaded(int page, List<News> newsList);

        void onError(Throwable error);
    }
}