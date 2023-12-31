package Jafari.Mahdi.JimCalssRoom;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MotionDetector {

    private boolean continueDetection = true;

    public MotionDetector(int cameraNumber) {
        while (continueDetection)
            startDetecting(cameraNumber);
    }

    public boolean isContinueDetection() {
        return continueDetection;
    }

    public void setContinueDetection(boolean continueDetection) {
        this.continueDetection = continueDetection;
    }


    public void startDetecting(int cameraNumber) {
        Mat frame = new Mat();
        Mat firstFrame = new Mat();
        Mat gray = new Mat();
        Mat frameDelta = new Mat();
        Mat thresh = new Mat();
        List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();


        VideoCapture camera = new VideoCapture();
        camera.open(cameraNumber); //open camera

        //set the video size to 512x288
        camera.set(3, 512);
        camera.set(4, 288);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        camera.read(frame);
        //convert to grayscale and set the first frame
        Imgproc.cvtColor(frame, firstFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);

        boolean noMotionDetected = true;
        while (noMotionDetected) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            camera.read(frame);

            //convert to grayscale
            Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);

            //compute difference between first frame and current frame
            Core.absdiff(firstFrame, gray, frameDelta);
            Imgproc.threshold(frameDelta, thresh, 25, 255, Imgproc.THRESH_BINARY);

            Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 2);
            Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            for (MatOfPoint cnt : cnts) {
                if (Imgproc.contourArea(cnt) < 500) {
                    continue;
                }

                System.out.println("Motion detected!!!");
                saveFrame(frame);
                noMotionDetected = false;
                break;
            }


        }

    }
    public void saveFrame (Mat frame){
        // Specify the directory for storing the captured frames
//        File tempDirectory = new File(System.getProperty("java.io.tmpdir"));
//        String outputDirectory = tempDirectory.getAbsolutePath();
        String outputDirectory = "C:\\Users\\Mahdi\\Desktop";

        // Create the directory if it doesn't exist
        File directory = new File(outputDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the frame as an image in the specified directory
        String outputFileName = "frame" + new Date().getTime() + ".jpg";
        String outputPath = outputDirectory + File.separator + outputFileName;
        Imgcodecs.imwrite(outputPath, frame);

        System.out.println("Frame saved successfully at: " + outputPath);
    }

}
