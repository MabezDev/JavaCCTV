package com.mabezdev.javacctv;

import com.sun.deploy.util.SystemUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;
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

    private ArrayList<Integer> connectedCameras;
    private ObservableList<CameraUI> cameraUI;

    @FXML
    private GridPane gridView;


    public static final String RECORD_LOCATION = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "MabezCCTV" + File.separator;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(System.getProperty("os.name").toLowerCase());
        if(System.getProperty("os.name").toLowerCase().contains("windows")) {
            if (System.getProperty("sun.arch.data.model").equals("32")) {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME + "_32");
            } else if (System.getProperty("sun.arch.data.model").equals("64")) {
                System.loadLibrary(Core.NATIVE_LIBRARY_NAME + "_64"); //currently just loading 32bit even for 64
            } else {
                System.out.println(System.getProperty("sun.arch.data.model") + " is not a supported architecture");
            }
        } else {
            //load linux dependancies
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        }
        System.out.println("Controller class initialized.");
        System.out.println("Path of recordings set to:");
        System.out.println(RECORD_LOCATION);
        makeDirectory(RECORD_LOCATION);

        testCodecs();

        cameraUI = FXCollections.observableArrayList();

        connectedCameras = getDevices(10);

        if(connectedCameras.size() == 0){
            showDialog("No camera's detected!","No camera's detected!","No camera's were found or are being used by another program, plug one or more in and restart the program.", Alert.AlertType.INFORMATION);
            System.exit(0);
        }

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

    private void testCodecs(){
        VideoWriter v = new VideoWriter();
        for(int i = 1; i < 100; i++){
            v.open("pls.mp4",i,12,new Size(20,20));
            if(v.isOpened()){
                System.out.println(i+ " is a valid codec.");
            }
        }
        v.release();

        File tester = new File(System.getProperty("user.dir") + File.separator+ "pls.mp4");
        if(tester.exists()){
            if(tester.delete()){
                System.out.println("Codec test file deleted!");
            } else {
                System.out.println("Failed to delete test file");
            }
        }
    }

    private void checkForMotion(){ //motion detection method
        BackgroundSubtractorMOG2 mog2 = Video.createBackgroundSubtractorMOG2();
    }

    public void stop(){
        cameraUI.forEach(c -> c.stop()); //stop all cameras, release from memory before shutdown (important as if the threads are not killed the program will hang!)
        System.out.println("Controller: Stopping application.");
    }

    public void showDialog(String windowTitle, String headerText, String text, Alert.AlertType type){
        Alert a = new Alert(type);
        a.setTitle(windowTitle);
        a.setHeaderText(headerText);
        a.setContentText(text);
        a.showAndWait();
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
