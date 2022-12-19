package com.project.rssreader;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;


public class MainController {
    private Stage stage;




    @FXML
    TextField inputText;


    //查看订阅
    public void fetchSource(ActionEvent event) throws SQLException, IOException, JDOMException {
        GetPost p = new GetPost();
        p.fetchSource(event);
    }







    //添加新的rss订阅，用于“添加订阅”按钮的setonaction
    public void addNewRecord(ActionEvent event) throws IOException, JDOMException {
            String rss = inputText.getText();
            AddSource a = new AddSource();
            a.insert(rss,inputText);
    }






    //点击后显示订阅源
    public void toSource(ActionEvent event)
    {

       DeleteSource d= new DeleteSource();
       d.refresh(event);

    }





}