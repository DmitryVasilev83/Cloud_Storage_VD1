package com.geekbrains.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent parent = FXMLLoader.load(getClass().getResource("client.fxml"));
        primaryStage.setTitle("Cloud Storage");
        primaryStage.setScene(new Scene(parent, 1280, 600));
        primaryStage.show();
    }
}

