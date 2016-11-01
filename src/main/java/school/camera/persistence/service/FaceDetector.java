package school.camera.persistence.service;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetector {

	public static void main(String[] args) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		System.out.println("\nRunning FaceDetector");
		//System.out.println(FaceDetector.class.getResource("haarcascade_frontalface_alt.xml").getPath().substring(1));
		CascadeClassifier faceDetector = new CascadeClassifier("D:/DownLoad/haarcascade_frontalface_alt.xml");
		Mat image = Highgui.imread("D:/DownLoad/face.jpg");

		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(image, faceDetections);

		System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

		for (Rect rect : faceDetections.toArray()) {
			Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
					new Scalar(0, 255, 0));
		}

		String filename = "D:/DownLoad/ouput.png";
		System.out.println(String.format("Writing %s", filename));
		Highgui.imwrite(filename, image);
	}
}