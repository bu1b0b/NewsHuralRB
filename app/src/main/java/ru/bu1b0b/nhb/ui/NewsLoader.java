package ru.bu1b0b.nhb.ui;

import android.util.Log;

import java.util.List;

import ru.bu1b0b.nhb.model.News;

class NewsLoader implements NewsAsyncTask.NewsLoaderListener {

    private static volatile NewsLoader instance;
    private boolean isLoading = false;
    private NewsAsyncTask newsAsyncTask;
    private NewsAsyncTask.NewsLoaderListener newsLoaderListener;

    static NewsLoader getInstance() {
        NewsLoader localInstance = instance;
        if (localInstance == null) {
            synchronized (NewsLoader.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new NewsLoader();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void onAlreadyLoading() {
        loaded();
        newsLoaderListener.onAlreadyLoading();
    }

    @Override
    public void onNewsLoaded(int page, List<News> newsList) {
        loaded();
        newsLoaderListener.onNewsLoaded(page, newsList);
    }

    @Override
    public void onError(Throwable error) {
        loaded();
        newsLoaderListener.onError(error);
    }

    void loadNews(int page, NewsAsyncTask.NewsLoaderListener listener) {
        if (isLoading) {
            listener.onAlreadyLoading();
        } else {
            isLoading = true;
            newsLoaderListener = listener;
            newsAsyncTask = new NewsAsyncTask(page, this);
            newsAsyncTask.execute();
        }
    }

    void cancelLoading() {
        Log.i("TAGG", "Cancel loading");
        if (newsAsyncTask != null) {
            newsAsyncTask.cancel(true);
            newsAsyncTask = null;
        }
    }

    private void loaded() {
        isLoading = false;
        newsAsyncTask = null;
    }
}

