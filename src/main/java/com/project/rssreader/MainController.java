package com.project.rssreader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import java.io.IOException;
import java.net.URL;
import java.sql.*;


public class MainController {
    private Stage stage;


    private WebView webView=new WebView();

    private WebEngine engine;

    @FXML
    TextField inputText;
    //添加新的rss订阅，用于“添加订阅”按钮的setonaction
    public void addNewRecord(ActionEvent event) throws IOException, JDOMException {
            String rss = inputText.getText();
            insert(rss);
    }
    Connection connection = null;
    //获取数据库存储
    public ResultSet getData() throws SQLException {

        try{
            connection = DriverManager.getConnection("jdbc:sqlite::resource:rss-records.sqlite");//需要是相对路径！！
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from rss_records");//获取所有订阅
            return rs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



//点击后看到订阅，调用getData()函数，解析并生成列表
    public void fetchSource(ActionEvent event) throws IOException, JDOMException, SQLException {

        ResultSet results = getData();
        ObservableList<Hyperlink> titles = FXCollections.observableArrayList();
        while(results.next())
        {
            String rss = results.getString("rss");//读取rss
            URL feedUrl = new URL(rss);
            SAXBuilder builder = new SAXBuilder();
            Element root = builder.build(feedUrl).getRootElement();


            //解析rss，提取title
            for (Element item : root.getChildren("channel").get(0).getChildren("item")) {
                RssItem newItem= new RssItem(item.getChildText("title"),item.getChildText("link"));
                Hyperlink itemLink=new Hyperlink(item.getChildText("title"));
                itemLink.setOnAction(e -> {
                    try {
                        showPost(newItem,e);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                titles.add(itemLink);
            }
        }
        connection.close();


        // 用listview呈现结果
        ListView<Hyperlink> listView = new ListView<Hyperlink>(titles);

        //加上返回按钮
        Button backBtn =new Button("返回主页");
        backBtn.setOnAction(e->{

           returnToMain(e);
        });

        // 把listview加在vbox中
        VBox root2 = new VBox();
        root2.getChildren().addAll(listView,backBtn);
        root2.setAlignment(Pos.CENTER);
        root2.setSpacing(5);
        root2.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene(root2, 800, 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        //获取原stage
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    //返回首页
    public void returnToMain(ActionEvent event)
    {
        Parent rootMain = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));//获取首页
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    //点击后显示订阅源
    public void toSource(ActionEvent event)
    {

       DeleteSource d= new DeleteSource();
       d.refresh(event);

    }

    //点击后显示全文，用webview呈现网页
    public void showPost(RssItem post,ActionEvent event) throws IOException {
        if (post == null) return;

        engine = webView.getEngine();
        engine.load(post.getLink());
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(webView);
//        VBox root = new VBox(webView);
        Scene scene = new Scene(scrollPane);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage= new Stage();
        stage.setResizable(true);
        stage.setFullScreen(true);

        stage.setScene(scene);
        stage.show();

    }


    //尝试连接订阅源，若可以连接，则向数据库内插入该订阅源
    public  void insert(String record) throws IOException, JDOMException {


        tryConnectSource(record);



        Connection connection=null;
        try{
            connection = DriverManager.getConnection("jdbc:sqlite::resource:rss-records.sqlite");
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into rss_records values('"+record+"')");
            inputText.setText("");
        } catch (SQLException e) {
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
    public  void tryConnectSource(String url) throws IOException, JDOMException {
        try{

            URL feedUrl = new URL(url);
            SAXBuilder builder = new SAXBuilder();
            Element root = builder.build(feedUrl).getRootElement();



            for (Element item : root.getChildren("channel").get(0).getChildren("item")) {
                RssItem newItem= new RssItem(item.getChildText("title"),item.getChildText("link"));
                Hyperlink itemLink=new Hyperlink(item.getChildText("title"));

            }
        } catch (JDOMException e) {
            inputText.setText("订阅失败");//若失败显示
            throw new RuntimeException(e);
        } catch (IOException e) {
            inputText.setText("订阅失败");
            throw new RuntimeException(e);
        }

    }
}