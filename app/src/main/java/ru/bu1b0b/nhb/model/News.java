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
    public int hashCode() {
        return title.hashCode();
    }
}
