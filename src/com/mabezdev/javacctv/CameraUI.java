package com.mabezdev.javacctv;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by Mabez on 03/08/2016.
 */
public class CameraUI extends VBox {

    @FXML
    private ChoiceBox<String> sourceChoice;

    @FXML
    private ImageView cameraImage;

    @FXML
    private Button recordButton;

    public CameraUI(){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "FXML/CameraUI.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void startRecording(ActionEvent event) {
        System.out.println("Button Pressed on CameraUI Controller");
    }
}
