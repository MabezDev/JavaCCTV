package com.mabezdev.javacctv;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.sleep;
import static com.mabezdev.javacctv.Utils.Utility.makeDirectory;

public class Controller implements Initializable{

    private boolean isMonitoring = false;
    private boolean isRecording = false;

    private Thread monitor;

    private ArrayList<Integer> connectedCameras;
    private ObservableList<CameraUI> cameraUI;

    @FXML
    private GridPane gridView;


    public static final String RECORD_LOCATION = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "MabezCCTV" + File.separator;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(System.getProperty("sun.arch.data.model").equals("32")) {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME+"_32");
        } else if(System.getProperty("sun.arch.data.model").equals("64")){
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME+"_64");
        } else {
            System.out.println(System.getProperty("sun.arch.data.model") + " is not a supported architecture");
        }
        System.out.println("Controller class initialized.");
        System.out.println("Path of recordings set to:");
        System.out.println(RECORD_LOCATION);
        makeDirectory(RECORD_LOCATION);


        cameraUI = FXCollections.observableArrayList();

        connectedCameras = getDevices(10);

        gridView.setPrefSize(Screen.getPrimary().getVisualBounds().getWidth(),Screen.getPrimary().getVisualBounds().getHeight());
        gridView.setHgap(10);
        gridView.setVgap(10);

        for(int i=0; i < connectedCameras.size(); i++){
            CameraUI cam = new CameraUI(connectedCameras.get(i));
            gridView.add(cam,0,i+1);
            cameraUI.add(cam);
        }
    }

    private ArrayList<Integer> getDevices(int numberOfDevicesToCheck){
        ArrayList<Integer> devices = new ArrayList<>();
        VideoCapture temp = new VideoCapture();
        for(int i = 0; i < numberOfDevicesToCheck; i++){
            temp.open(i);
            if(temp.isOpened()){
                devices.add(i);
            }
        }
        System.out.println("Camera Search complete, "+devices.size()+" camera(s) found!");
        temp.release();
        return devices;
    }

    private void updateDisplay(ImageView view,Image frameToDraw){
        if(frameToDraw != null) {
            view.setImage(frameToDraw);
        }
    }

    private void testCodecs(){
        VideoWriter v = new VideoWriter();
        for(int i = 1; i < 100; i++){
            v.open("pls.mp4",i,12,new Size(20,20));
            if(v.isOpened()){
                System.out.println(i+ " is a valid codec.");
            }
        }
    }

    @FXML
    void startMonitoring(ActionEvent event) {
        System.out.println("Monitor Button Pressed.");
        isMonitoring = !isMonitoring;
        if(isMonitoring){
            //monitor_btn.setText("Stop Monitoring");
            monitor = new Thread(() -> {
                while(isMonitoring){
                    //updateDisplay(camLeft,cameraObjects.get(0).getCurrentImage());
                    //updateDisplay(camRight,cameraObjects.get(0).getCurrentImage());

                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                    }
                }
            });
            monitor.start();
        } else {
            //monitor_btn.setText("Monitor");
            monitor.interrupt();
        }

    }

    public void stop(){
        if(isMonitoring){
            isMonitoring = false;//halt loop
            monitor.interrupt();//kill thread
        }
        cameraUI.forEach(c -> c.stop());
        System.out.println("Controller: Stopping application.");
    }

    private class Codec{
        private String name;
        private int ID;

        private Codec(String name, int id){
            this.name = name;
            this.ID = id;
        }

        public int getId(){
            return ID;
        }

        public String getName(){
            return name;
        }

    }




}
