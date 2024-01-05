package Jafari.Mahdi.JimCalssRoom;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
    Thread thread;

    @GetMapping
    public ModelAndView init(@ModelAttribute ModelMap map) {
        return new ModelAndView("main", map);
    }

    @PostMapping("/manageDetection/{classNumber}/{saveStatus}/{detectionStatus}")
    @ResponseBody
    public boolean manageDetection(@PathVariable int classNumber, @PathVariable boolean saveStatus, @PathVariable boolean detectionStatus) {
        MotionDetector.saveImages = saveStatus;
        MotionDetector.cameraNumber = classNumber;
        if (detectionStatus) {
            MotionDetector.continueDetection = true;
            thread = new Thread(() -> new MotionDetector());
            thread.start();
        } else {
            MotionDetector.continueDetection = false;
            thread.interrupt();
        }

        return true;
    }
}
