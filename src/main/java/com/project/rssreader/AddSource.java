package com.project.rssreader;

import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class AddSource {
    //尝试连接订阅源，若可以连接，则向数据库内插入该订阅源
    public  void insert(String record, TextField text) throws IOException, JDOMException {


        tryConnectSource(record,text);



        Connection connection=null;
        try{
            connection = DriverManager.getConnection("jdbc:sqlite::resource:rss-records.sqlite");
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into rss_records values('"+record+"')");
            text.setText("");
        } catch (SQLException e) {
            text.setText("已添加过该订阅");//数据库已有
            throw new RuntimeException(e);
        }
        try
        {
            if(connection != null)
                connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }



    //用于尝试连接订阅源
    public  void tryConnectSource(String url,TextField text) throws IOException, JDOMException {
        try{

            URL feedUrl = new URL(url);
            SAXBuilder builder = new SAXBuilder();
            Element root = builder.build(feedUrl).getRootElement();



            for (Element item : root.getChildren("channel").get(0).getChildren("item")) {
                RssItem newItem= new RssItem(item.getChildText("title"),item.getChildText("link"));
                Hyperlink itemLink=new Hyperlink(item.getChildText("title"));

            }
        } catch (JDOMException e) {
            text.setText("订阅失败");//若失败显示
            throw new RuntimeException(e);
        } catch (IOException e) {
            text.setText("订阅失败");
            throw new RuntimeException(e);
        }

    }
}
