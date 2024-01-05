package Jafari.Mahdi.JimCalssRoom;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MotionDetector {

    public static boolean continueDetection = false;
    public static boolean saveImages = false;
    public static int cameraNumber = 0;
    VideoCapture camera = new VideoCapture();


    public MotionDetector() {
        camera.open(cameraNumber);
        while (continueDetection)
            startDetecting();
        camera.release();
    }


    public void startDetecting() {
        Mat frame = new Mat();
        Mat firstFrame = new Mat();
        Mat gray = new Mat();
        Mat frameDelta = new Mat();
        Mat thresh = new Mat();
        List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();


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
sendStatus();
                    if (saveImages)
                        saveFrame(frame);
                    noMotionDetected = false;
                    break;
                }


            }

    }

    public void saveFrame(Mat frame) {
        String outputDirectory = "C:\\Users\\Mahdi\\Desktop\\MotionDetection";

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

    private void sendStatus()
    {
        final String uri = "http://localhost:8080/class/";

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        System.out.println(result);
    }

}
