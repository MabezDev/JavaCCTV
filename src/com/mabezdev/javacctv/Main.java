package com.mabezdev.javacctv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Controller mainController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("FXML/Controller.fxml"));
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "FXML/Controller.fxml"
                )
        );
        loader.load();
        mainController = loader.getController();
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
