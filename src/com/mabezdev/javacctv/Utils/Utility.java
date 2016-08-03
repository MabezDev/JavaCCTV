package com.mabezdev.javacctv.Utils;

import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * Created by Mabez on 01/08/2016.
 */
public class Utility {

    public static Image mat2Image(Mat frame)
    {
        if(!frame.empty()) {
            // create a temporary buffer
            MatOfByte buffer = new MatOfByte();
            // encode the frame in the buffer
            Imgcodecs.imencode(".png", frame, buffer);
            // build and return an Image created from the image encoded in the
            // buffer
            return new Image(new ByteArrayInputStream(buffer.toArray()));
        }
        return null;
    }

    public static String makeDirectory(String fullPath) {
        File home = new File(fullPath);
        if(!home.exists()){
            boolean success = home.mkdir();
            if(success){
                System.out.println("Folder creation success!");
            } else {
                System.out.println("Folder failed to create.");
            }
        }
        return home.getAbsolutePath() + File.separator;
    }
}
