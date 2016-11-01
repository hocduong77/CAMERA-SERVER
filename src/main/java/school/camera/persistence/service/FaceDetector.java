package school.camera.persistence.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.dao.IImageRepo;
import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Image;

public class FaceDetector implements Runnable {

	public CameraRepo cameraRepo;
	public IImageRepo imageRepo;
	public Long cameraId;
	public Thread thread;
	public Mat frame;

	private void faceDetector() {

		CascadeClassifier faceDetector = new CascadeClassifier("D:/DownLoad/haarcascade_frontalface_alt.xml");

		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(frame, faceDetections);

		if (faceDetections.toArray().length >= 1) {
			// this.captureTime = new Date();
			for (Rect rect : faceDetections.toArray()) {
				Core.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
						new Scalar(0, 255, 0));
			}
			DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
			String imageName = cameraId.toString() + "_" + df.format(new Date()) + ".png";
			String fileName = "C:/Users/BinhHoc/Documents/GitHub/CAMERA-SERVER/src/main/webapp/resources/images/"
					+ imageName;
			System.out.println(String.format("Writing %s", fileName));
			Highgui.imwrite(fileName, frame);
			Camera camera = cameraRepo.findByCameraid(cameraId);
			Image image = new Image();
			image.setCamera(camera);
			image.setDate(new Date());
			String result = "http://localhost:8080/images/" + imageName;
			image.setImageUrl(result);
			imageRepo.save(image);
			System.out.println(String.format("save Detected %s faces", faceDetections.toArray().length));
		} else {
			return;
		}

	}

	@Override
	public void run() {
		faceDetector();
	}

	public void start() {
		System.out.println("Starting opencv ");
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
}