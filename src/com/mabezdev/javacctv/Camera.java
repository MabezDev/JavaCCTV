package com.mabezdev.javacctv;


import com.mabezdev.javacctv.Controller;
import com.mabezdev.javacctv.Utils.Utility;
import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;

import java.io.File;
import java.util.Date;

import static java.lang.Thread.sleep;

/**
 * Created by Mabez on 01/08/2016.
 */
public class Camera {

    private Image currentImage;
    private VideoCapture capture;
    private VideoWriter recorder;

    private boolean isRecording = false;
    private boolean isOpen = false;

    private Thread main;

    private int cameraId;
    private int FPS = 12;


    public Camera(int cameraID,int fps){
        this.cameraId = cameraID;
        this.FPS = fps;

        capture = new VideoCapture();
    }

    public void startRecording(){
        isRecording = true;
        //open file, setup encoders
        String folder = Utility.makeDirectory(Controller.RECORD_LOCATION+ File.separator + Controller.dateOnly.format(new Date()));
        String time = Controller.timeOnly.format(new Date());
        //System.out.println("final: "+folder + time +".mp4");

        recorder = new VideoWriter();
            /*
                IMPORTANT: remember to play opencv_ffmpeg*.dll in build path otherwise this will silently fail to open all videos
             */
        recorder.open(folder + time + " CAM "+ cameraId +".mp4", Controller.MP4_CODEC_ID,12,getSize());
        if(recorder.isOpened()){
            System.out.println("Writer opened!");
        } else {
            System.out.println("Writer failed to open file.");
        }
    }

    public void stopRecording(){
        isRecording = false;
        recorder.release();
    }

    public Image getCurrentImage(){
        return currentImage;
    }

    public Size getSize(){
        if(capture.isOpened()){
            Mat hw = new Mat();
            capture.read(hw);
            return new Size(hw.width(),hw.height());
        }
        return new Size(10,10);
    }

    public void openCamera(int id){

        capture.open(id);

        if(capture.isOpened()){
            isOpen = true;
            System.out.println("Camera with id: "+ id+ " opened!");
        }
        main = new Thread(() -> {
            while (isOpen) {
                Mat currentFrame = grabFrame();
                if(currentFrame != null) {
                    currentImage = Utility.mat2Image(currentFrame);
                    if(isRecording){
                        //write to file
                        if(recorder.isOpened()){
                            recorder.write(currentFrame);
                        }
                    }
                }
                try {
                    sleep(15);
                } catch (InterruptedException e) {
                }
            }
        });
        main.start();
    }

    public void openCamera(){
        openCamera(cameraId);
    }

    public void closeCamera(){
        isOpen = false;
        capture.release();
        main.interrupt();
    }

    private Mat grabFrame(){
        Mat frame = new Mat();
        if(capture.isOpened()){
            capture.read(frame);
            if(!frame.empty()){
                Imgproc.putText(frame, Controller.timeOnly.format(new Date()) + "   " + Controller.dateOnly.format(new Date()),new Point(290,440), Core.FONT_HERSHEY_PLAIN, 2.0, new Scalar(200,200,200));
            }
        }
        return frame;
    }

}
