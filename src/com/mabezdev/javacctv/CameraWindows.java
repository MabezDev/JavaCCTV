package com.mabezdev.javacctv;

import com.mabezdev.javacctv.Utils.Utility;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

import static com.mabezdev.javacctv.Utils.Utility.dateOnly;
import static com.mabezdev.javacctv.Utils.Utility.timeOnly;
import static java.lang.Thread.sleep;

/**
 * Created by mabez on 07/08/2016.
 */
public class CameraWindows {

    private FrameGrabber grabber;
    private FrameRecorder recorder;

    private String cameraName;
    private int cameraId;
    private int fps;

    private boolean isRecording = false;
    private boolean isOpen = false;

    private Thread main;

    private Frame currentFrame;
    private Image currentImage;

    public static Java2DFrameConverter frameToBufferedImage = new Java2DFrameConverter();



    public CameraWindows(String cameraName, int cameraIndex, int fps){
        this.cameraId = cameraIndex;
        this.cameraName = cameraName;
        this.fps = fps;

        grabber = new FFmpegFrameGrabber("video="+cameraName);
        //dshow is know to work with almost all devices, even knock off easycaps
        grabber.setFormat("dshow");
        // if a camera with the same name is detected we use a index to differentiate between them
        //grabber.setOption("video_device_number",Integer.toString(cameraIndex));

        if(!System.getProperty("os.name").equals("Windows 10")){
            grabber.setOption("crossbar_video_input_pin_number","1"); //win 7 and below get easycap audio the wrong way around, eventually add a setting system so they can choose and this program will remember
            System.out.println("Older OS Detected, fixing Crossbar settings.");
        }
    }

    public void openCamera(){
        try {
            grabber.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        isOpen = true;
        main = new Thread(() -> {
            while (isOpen) {
                try {
                    Frame pure = grabber.grab();
                    if(pure==null){
                        System.out.println("Raw Frame null");
                    }
                    currentFrame = addOverlay(pure);
                    setCurrentImage(currentFrame);
                    if (currentFrame != null) {
                        if (isRecording) {
                            //write to file
                            recorder.record(currentFrame);
                        }
                        // add the overlay after we have written the frame to disk
                        //currentImage = Utility.mat2Image(addMonitorOverlay(currentFrame));
                    }
                } catch (FrameRecorder.Exception e){
                    e.printStackTrace();
                }  catch (FrameGrabber.Exception e2){
                    e2.printStackTrace();
                }
                try {
                    sleep(15);
                } catch (InterruptedException e) {
                }
            }
        });
        main.start();

    }

    private Frame addOverlay(Frame img){
        BufferedImage bufferedImage = frameToBufferedImage.getBufferedImage(img);
        Graphics g = bufferedImage.getGraphics();
        g.setFont(new Font("Arial Black", Font.PLAIN, 20));
        g.drawString(timeOnly.format(new Date())+"   "+ dateOnly.format(new Date()),grabber.getImageWidth() - 250,grabber.getImageHeight() - 20);
        Frame toReturn = frameToBufferedImage.getFrame(bufferedImage);
        return toReturn;
    }

    public void closeCamera(){
        if(isRecording){
            stopRecording();
        }
        try {
            grabber.stop();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }
        isOpen = false;
        main.interrupt();
    }

    public void startRecording(){
        String folder = Utility.makeDirectory(Controller.RECORD_LOCATION+ File.separator + dateOnly.format(new Date()));
        String time = timeOnly.format(new Date());
        recorder = new FFmpegFrameRecorder(folder + time + cameraName + " " + Integer.toString(cameraId) +".avi",grabber.getImageWidth(),grabber.getImageHeight());
        recorder.setVideoCodec(13);
        recorder.setFrameRate(fps);

        try {
            recorder.start();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }

        isRecording = true;
    }

    public void stopRecording(){
        try {
            recorder.stop();
        } catch (FrameRecorder.Exception e) {
            e.printStackTrace();
        }
        isRecording = false;
    }

    public Image getCurrentImage(){
        return currentImage;
    }

    private void setCurrentImage(Frame f){
        BufferedImage image = frameToBufferedImage.getBufferedImage(f);
        if(image == null){
            System.out.println("Getting buffered image is null");
        }
        currentImage = SwingFXUtils.toFXImage(image,null);
    }
}
