package com.mabezdev.javacctv;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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

    @FXML
    private Pane pane;

    private CameraWindows camera;
    private boolean isRunning = true;
    private Thread run;
    private boolean isRecording = false;

    public CameraUI(String cameraName,int cameraIndex){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "FXML/CameraUI.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        pane.setStyle("-fx-background-color:darkslategrey");

        //will most likely remove the choice box and auto open every camera it can find

        //camera = new Camera(cameraIndex,12);//zero will be replaced with choice from ChoiceBox, not sure if we should have a fixed fps or not
        camera = new CameraWindows(cameraName,cameraIndex,12);
        camera.openCamera();

        run = new Thread(() -> { //refreshes the preview, on thread so we not lock the ui
           while(isRunning){
               Image image = camera.getCurrentImage();
               if(image != null) {
                   cameraImage.setImage(image);
               }
               try {
                   Thread.sleep(60);
               } catch (InterruptedException e) {
               }
           }
        });
        run.start();
    }

    @FXML
    void toggleRecording(ActionEvent event) {
        isRecording = !isRecording;
        if(isRecording){
            recordButton.setText("Stop Recording");
            camera.startRecording();
        } else {
            recordButton.setText("Record");
            camera.stopRecording();
        }
    }

    public void stop(){
        isRunning = false;
        run.interrupt();
        camera.closeCamera();
    }

}
