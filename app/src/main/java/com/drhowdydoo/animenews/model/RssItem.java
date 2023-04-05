package com.drhowdydoo.animenews.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.tickaroo.tikxml.annotation.PropertyElement;
import com.tickaroo.tikxml.annotation.Xml;

@Xml
@Entity
public class RssItem {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @PropertyElement(name = "title")
    @ColumnInfo(name = "title")
    private String title;
    @PropertyElement(name = "link")
    @ColumnInfo(name = "link")
    private String link;
    @PropertyElement(name = "guid")
    @ColumnInfo(name = "guid")
    private String guid;
    @PropertyElement(name = "description")
    @ColumnInfo(name = "description")
    private String description;
    @PropertyElement(name = "pubDate")
    @ColumnInfo(name = "pub_date")
    private String pubDate;
    @PropertyElement(name = "category")
    @ColumnInfo(name = "category")
    private String category;
    @PropertyElement(name = "media:thumbnail")
    @ColumnInfo(name = "image_url")
    private String imageUrl;

    public RssItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
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
}
