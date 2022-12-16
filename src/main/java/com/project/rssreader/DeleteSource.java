package com.project.rssreader;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class DeleteSource {
    private Stage stage;

    ListView<String> rssList;


    //实现删除数据库中被选中的源
    public void deleteSource(ActionEvent event)
    {
        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:src\\main\\resources\\rss-records.sqlite");
            Statement statement = connection.createStatement();
            String selectedIndex = rssList.getSelectionModel().getSelectedItem();//获取被选则的源的字符串字段

            System.out.println(selectedIndex);
            statement.executeUpdate("delete from rss_records where rss='"+selectedIndex+"'");
            connection.close();
            DeleteSource d= new DeleteSource();
            d.refresh(event);//刷新查看结果


        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }


    }

    //返回主页
    public void returnTo(ActionEvent event)
    {
        Parent rootMain = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
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

    //用于显示订阅源，和刷新查看订阅源变化
    public  void refresh(ActionEvent event)
    {
        try{
            Connection connection = DriverManager.getConnection("jdbc:sqlite:src\\main\\resources\\rss-records.sqlite");
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select * from rss_records");
            ObservableList<String> sources = FXCollections.observableArrayList();
            while(rs.next()) {
                String rss = rs.getString("rss");
                sources.add(rss);
            }
            connection.close();


            rssList = new ListView<>(sources);
            Button backBtn =new Button("返回主页");
            backBtn.setOnAction(e->{

                returnTo(e);
            });
            Button deleteBtn =new Button("删除该订阅");
            deleteBtn.setOnAction(e->{

                deleteSource(e);
            });


            GridPane root = new GridPane();
            root.add(rssList,0,0,2,1);
            root.add(backBtn,1,1);
            root.add(deleteBtn,0,1);
            root.setPadding(new Insets(10, 10, 10, 10));
            root.setVgap(10);
            root.setHgap(105);
            root.setAlignment(Pos.CENTER);



            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            //获取原stage
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }


}
