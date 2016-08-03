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

    private Camera camera;
    private boolean isRunning = true;
    private Thread run;
    private boolean isRecording = false;

    public CameraUI(int cameraIndex){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "FXML/CameraUI.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        //will most likely remove the choice box and auto open every camera it can find

        camera = new Camera(cameraIndex,12);//zero will be replaced with choice from ChoiceBox, not sure if we should have a fixed fps or not
        camera.openCamera();

        run = new Thread(() -> { //refreshes the preview
           while(isRunning){
                cameraImage.setImage(camera.getCurrentImage());
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
