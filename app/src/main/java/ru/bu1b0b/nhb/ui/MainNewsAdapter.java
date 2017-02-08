package ru.bu1b0b.nhb.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.bu1b0b.nhb.R;
import ru.bu1b0b.nhb.model.News;


public class MainNewsAdapter extends RecyclerView.Adapter<MainNewsAdapter.NewsViewHolder> {

    private List<News> newsList = new ArrayList<>();
    private NewsListener listener;

    MainNewsAdapter(NewsListener listener) {
        this.listener = listener;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View rootView = inflater.inflate(R.layout.list_item, parent, false);
        return new NewsViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.bindNews(news);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    void addRecentNews(List<News> newsList) {
        for (int i = newsList.size() - 1; i >= 0; i--) {
            News news = newsList.get(i);
            if (!isContained(news)) {
                this.newsList.add(0, news);
            }
        }
        notifyDataSetChanged();
    }

    void addOldNews(List<News> newsList) {
        for (News news : newsList) {
            if (!isContained(news)) {
                this.newsList.add(news);
            }
        }
        notifyDataSetChanged();
    }

    private boolean isContained(News news) {
        boolean contained = false;
        for (News s : newsList) {
            if (s.equals(news)) {
                contained = true;
                break;
            }
        }
        return contained;
    }

    public interface NewsListener {

        void onNewsClicked(News news);
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {

        public TextView news_title;
        public TextView news_published;

        public NewsViewHolder(View itemView) {
            super(itemView);
            news_title = (TextView) itemView.findViewById(R.id.card_news_title);
            news_published = (TextView) itemView.findViewById(R.id.card_news_published);
        }

        void bindNews(final News news) {
            news_title.setText(news.getTitle());
            news_published.setText(news.getPublished());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onNewsClicked(news);
                }
            });
        }
    }
}