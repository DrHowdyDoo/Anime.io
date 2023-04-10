package com.drhowdydoo.animenews.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.drhowdydoo.animenews.util.DateConverter;
import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

import java.time.ZonedDateTime;
import java.util.Objects;

@Xml
@Entity
public class RssItem {

    @PropertyElement(name = "title")
    @ColumnInfo(name = "title")
    private String title;
    @PropertyElement(name = "link")
    @ColumnInfo(name = "link")
    private String link;
    @PropertyElement(name = "guid")
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "guid")
    private String guid;
    @PropertyElement(name = "description")
    @ColumnInfo(name = "description")
    private String description;
    @PropertyElement(name = "pubDate", converter = DateConverter.class)
    @ColumnInfo(name = "pub_date")
    private ZonedDateTime pubDate;
    @PropertyElement(name = "category")
    @ColumnInfo(name = "category")
    private String category;
    @PropertyElement(name = "media:thumbnail")
    @ColumnInfo(name = "image_url")
    private String imageUrl;

    public RssItem() {
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getPubDate() {
        return pubDate;
    }

    public void setPubDate(ZonedDateTime pubDate) {
        this.pubDate = pubDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public String toString() {
        return "RssItem{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", guid='" + guid + '\'' +
                ", description='" + description + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", category='" + category + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RssItem rssItem = (RssItem) o;
        return Objects.equals(title, rssItem.title) && Objects.equals(link, rssItem.link) && guid.equals(rssItem.guid) && Objects.equals(description, rssItem.description) && Objects.equals(pubDate, rssItem.pubDate) && Objects.equals(category, rssItem.category) && Objects.equals(imageUrl, rssItem.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, link, guid, description, pubDate, category, imageUrl);
    }
}
