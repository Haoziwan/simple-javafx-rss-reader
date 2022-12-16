module com.project.rssreader {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires org.jdom2;
    requires javafx.web;
    requires java.sql;


    opens com.project.rssreader to javafx.fxml;
    exports com.project.rssreader;
}