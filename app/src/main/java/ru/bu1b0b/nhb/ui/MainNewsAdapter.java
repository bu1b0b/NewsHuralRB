package ru.bu1b0b.nhb.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.bu1b0b.nhb.R;
import ru.bu1b0b.nhb.model.News;
import ru.bu1b0b.nhb.ui.news.NewsActivity;


public class MainNewsAdapter extends RecyclerView.Adapter<MainNewsAdapter.NewsViewHolder> {

    private Context context;
    private List<News> newsList;
    private LayoutInflater inflater;

    public MainNewsAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = inflater.inflate(R.layout.list_item, parent, false);
        return new NewsViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, final int position) {
        News currentNews = newsList.get(position);
        holder.newsTitle.setText(currentNews.getTitle());
        holder.newsPublished.setText(currentNews.getPublished());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsActivity.class);
                intent.putExtra("newsTitle", newsList.get(position).getTitle());
                intent.putExtra("newsLink", newsList.get(position).getLinks());
                intent.putExtra("newsPublished", newsList.get(position).getPublished());
                ((MainActivity) context).startInterstitialAd(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        TextView newsTitle;
        TextView newsPublished;

        NewsViewHolder(View itemView) {
            super(itemView);
            newsTitle = (TextView) itemView.findViewById(R.id.card_news_title);
            newsPublished = (TextView) itemView.findViewById(R.id.card_news_published);
        }

    }
}