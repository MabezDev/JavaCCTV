package com.mabezdev.javacctv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller mainController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "FXML/Controller.fxml"
                )
        );
        loader.load();

        GridPane root = loader.getRoot();
        mainController = loader.getController();

        primaryStage.setMaximized(true);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop(){
        mainController.stop();

        System.exit(0);
    }
}
