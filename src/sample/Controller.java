package sample;

import com.github.sarxos.webcam.Webcam;
import com.sun.org.apache.regexp.internal.RE;
import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.sleep;
import static sample.Utils.makeDirectory;

public class Controller implements Initializable{

    private boolean isMonitoring = false;
    private boolean isRecording = false;

    private Thread monitor;
    private SimpleDateFormat dateFormat = new SimpleDateFormat ("dd.MM.yy HH:mm:ss");
    public static final SimpleDateFormat timeOnly = new SimpleDateFormat ("HH.mm.ss");
    public static final SimpleDateFormat dateOnly = new SimpleDateFormat ("dd.MM.yy");
    public static final int MP4_CODEC_ID = 66;

    private ArrayList<Camera> cameraObjects;
    private HashMap<String,Integer> connectedCameras;


    public static final String RECORD_LOCATION = FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + File.separator + "MabezCCTV" + File.separator;

    @FXML
    private ChoiceBox<String> sourceChoiceLeft;

    @FXML
    private ChoiceBox<String> sourceChoiceRight;

    @FXML
    private ImageView camLeft;

    @FXML
    private ImageView camRight;

    @FXML
    private Button record_button;

    @FXML
    private Button monitor_btn;

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
        connectedCameras = getDevices(10);
        ObservableList<String> devices = FXCollections.observableArrayList(connectedCameras.keySet());
        sourceChoiceLeft.setItems(devices);
        sourceChoiceRight.setItems(devices);

        cameraObjects = new ArrayList<>();

        for(int i = 0; i < connectedCameras.size(); i++) {
            sourceChoiceLeft.setValue(devices.get(i));
            cameraObjects.add(new Camera(i,12));
        }

        for(Camera c : cameraObjects){
            c.openCamera();
        }

        monitor_btn.fire();
    }

    private HashMap<String,Integer> getDevices(int numberOfDevicesToCheck){
        HashMap<String,Integer> devices = new HashMap<>();
        VideoCapture temp = new VideoCapture();
        for(int i = 0; i < numberOfDevicesToCheck; i++){
            temp.open(i);
            if(temp.isOpened()){
                devices.put("Camera "+Integer.toString(i), i);
            }
        }
        System.out.println("Camera Search complete, "+devices.size()+" camera(s) found!");
        temp.release();
        return devices;
    }

    @FXML
    void startRecording(ActionEvent event) {
        System.out.println("Record Button Pressed.");
        isRecording = !isRecording;
        if(isRecording){
            record_button.setText("Stop Recording");
            cameraObjects.get(0).startRecording();
        } else {
            record_button.setText("Record");
            cameraObjects.get(0).stopRecording();
        }
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
            monitor_btn.setText("Stop Monitoring");
            monitor = new Thread(() -> {
                while(isMonitoring){
                    updateDisplay(camLeft,cameraObjects.get(0).getCurrentImage());
                }
            });
            monitor.start();
        } else {
            monitor_btn.setText("Monitor");
            monitor.interrupt();
        }

    }

    public void stop(){
        if(isMonitoring){
            isMonitoring = false;//halt loop
            monitor.interrupt();//kill thread
        }
        for(Camera c : cameraObjects){
            c.closeCamera();
        }
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
