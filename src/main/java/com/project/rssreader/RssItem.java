package com.project.rssreader;
//存储数据的标题和链接
public class RssItem {
    private String title;
    private String link;

    public RssItem(String title,String link) {
        this.title = title;
        this.link = link;
    }
    public String getTitle()
    {
        return this.title;
    }
    public String getLink()
    {
        return this.link;
    }
}
