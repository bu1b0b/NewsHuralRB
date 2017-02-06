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
import ru.bu1b0b.nhb.ui.news.NewsActivitity;


public class MainNewsAdapter extends RecyclerView.Adapter<MainNewsAdapter.NewsViewHolder> {

    private List<News> newsList;
    private Context context;
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
        holder.news_title.setText(currentNews.getTitle());
        holder.news_published.setText(currentNews.getPublished());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsActivitity.class);
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

    public class NewsViewHolder extends RecyclerView.ViewHolder {

        public TextView news_title;
        public TextView news_published;

        public NewsViewHolder(View itemView) {
            super(itemView);
            news_title = (TextView) itemView.findViewById(R.id.card_news_title);
            news_published = (TextView) itemView.findViewById(R.id.card_news_published);
        }

    }
}