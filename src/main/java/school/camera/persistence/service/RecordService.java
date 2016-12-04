package school.camera.persistence.service;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class RecordService {

	private static final double FRAME_RATE = 50;

	private static final int SECONDS_TO_RUN_FOR = 100;

	private static final String outputFilename = "D:/DownLoad/stream.mp4";

	private static Dimension screenBounds;

	public static void main(String[] args) {

		// let's make a IMediaWriter to write the file.
		final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);

		screenBounds = Toolkit.getDefaultToolkit().getScreenSize();

		// We tell it we're going to add one video stream, with id 0,
		// at position 0, and that it will have a fixed frame rate of
		// FRAME_RATE.
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, screenBounds.width / 2, screenBounds.height / 2);

		long startTime = System.nanoTime();

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		VideoCapture camera = new VideoCapture("rtsp://192.168.1.101:554/live.sdp");
		//
		Mat frame = new Mat();
		// camera.read(frame);
		// if (!camera.isOpened()) {
		// System.out.println("Error");
		// } else {
		// while (true) {
		//
		// if (camera.read(frame)) {
		// System.out.println("read");
		// BufferedImage image = MatToBufferedImage(frame);
		//
		// // convert to the right image type
		// BufferedImage bgrScreen = convertToType(image,
		// BufferedImage.TYPE_3BYTE_BGR);
		//
		// // encode the image to stream #0
		// writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime,
		// TimeUnit.NANOSECONDS);
		//
		// // sleep for frame rate milliseconds
		// try {
		// System.out.println("sleep");
		// Thread.sleep((long) (1000 / FRAME_RATE));
		// } catch (InterruptedException e) {
		// // ignore
		// }
		// }
		// }
		// }
		// writer.close();
		for (int index = 0; index < SECONDS_TO_RUN_FOR * FRAME_RATE; index++) {

			if (camera.read(frame)) {
				// take the screen shot
				BufferedImage screen = MatToBufferedImage(frame);
				// convert to the right image type
				BufferedImage bgrScreen = convertToType(screen, 5);

				// encode the image to stream #0
				writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);

				// sleep for frame rate milliseconds
				try {
					Thread.sleep((long) (1000 / FRAME_RATE));
				} catch (InterruptedException e) {
					// ignore
				}

			}

		}

		// tell the writer to close and write the trailer if needed
		System.out.println("end");
		writer.close();

	}

	public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {

		BufferedImage image;

		// if the source image is already the target type, return the source
		// image
		if (sourceImage.getType() == targetType) {
			image = sourceImage;
		}
		// otherwise create a new image of the target type and draw the new
		// image
		else {
			image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
			image.getGraphics().drawImage(sourceImage, 0, 0, null);
		}

		return image;

	}

	private static BufferedImage getDesktopScreenshot() {
		try {
			Robot robot = new Robot();
			Rectangle captureSize = new Rectangle(screenBounds);
			return robot.createScreenCapture(captureSize);
		} catch (AWTException e) {
			e.printStackTrace();
			return null;
		}

	}

	private static BufferedImage MatToBufferedImage(Mat frame) {
		// Mat() to BufferedImage
		int type = 0;
		if (frame.channels() == 1) {
			type = BufferedImage.TYPE_BYTE_GRAY;
		} else if (frame.channels() == 3) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
		WritableRaster raster = image.getRaster();
		DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
		byte[] data = dataBuffer.getData();
		frame.get(0, 0, data);

		return image;
	}
}