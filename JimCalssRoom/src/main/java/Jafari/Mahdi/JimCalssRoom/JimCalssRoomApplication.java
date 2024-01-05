package Jafari.Mahdi.JimCalssRoom;

import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JimCalssRoomApplication {

	public static void main(String[] args) {
		SpringApplication.run(JimCalssRoomApplication.class, args);
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

}
