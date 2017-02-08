package ru.bu1b0b.nhb.model;

/**
 * Created by bu1b0b on 01.02.2017.
 */

public class News {

    private String title;
    private String published;
    private String links;

    public News() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublished() {
        return published;
    }

    public void setPublished(String published) {
        this.published = published;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        News news = (News) o;

        return (title != null ? title.equals(news.title) : news.title == null) &&
                (published != null ? published.equals(news.published) : news.published == null) &&
                (links != null ? links.equals(news.links) : news.links == null);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (published != null ? published.hashCode() : 0);
        result = 31 * result + (links != null ? links.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "News{title=" + title + "}";
    }
}
