package school.camera.persistence.service;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.dao.IVideoRepo;
import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Video;

@SuppressWarnings({ "checkstyle:magicnumber", "PMD.LawOfDemeter", "PMD.AvoidLiteralsInIfCondition",
		"PMD.AvoidInstantiatingObjectsInLoops", "PMD.AvoidUsingNativeCode", "PMD.AvoidFinalLocalVariable",
		"PMD.CommentSize", "PMD.AvoidPrintStackTrace", "PMD.UseProperClassLoader", "PMD.AvoidPrefixingMethodParameters",
		"PMD.DataflowAnomalyAnalysis" })
public class RecordServer implements Runnable {

	public IVideoRepo videoRepo;

	public CameraRepo cameraRepo;

	public boolean stop = false;

	public Thread thread;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * Kernel used for contours.
	 */
	private final Mat CONTOUR_KERNEL = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3, 3),
			new Point(1, 1));
	/**
	 * Contour hierarchy.
	 */
	private final Mat HIERARCHY = new Mat();
	/**
	 * Point used for contour dilate and erode.
	 */
	private final Point CONTOUR_POINT = new Point(-1, -1);

	/**
	 * Suppress default constructor for noninstantiability.
	 */
	public List<Rect> contours(final Mat source) {
		Imgproc.dilate(source, source, CONTOUR_KERNEL, CONTOUR_POINT, 15);
		Imgproc.erode(source, source, CONTOUR_KERNEL, CONTOUR_POINT, 10);
		final List<MatOfPoint> contoursList = new ArrayList<MatOfPoint>();
		Imgproc.findContours(source, contoursList, HIERARCHY, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		final List<Rect> rectList = new ArrayList<Rect>();
		// Convert MatOfPoint to Rectangles
		for (final MatOfPoint mop : contoursList) {
			rectList.add(Imgproc.boundingRect(mop));
			// Release native memory
		}
		return rectList;
	}

	String url; // String url = "rtsp://192.168.1.100:554/live.sdp";

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	// Video variables:
	// ----------------
	int imagenb = 0; // image nb of the image currently transmitted
	VideoStream video; // VideoStream object used to access video frames
	int MJPEG_TYPE = 26; // RTP payload type for MJPEG video
	int FRAME_PERIOD = 100; // Frame period of the video to stream, in ms
	int VIDEO_LENGTH = 500; // length of the video in frames

	public double objectWith;

	public double objectHeight;

	public Long cameraId;

	boolean motion;

	Date timeMotion;

	Date recordTime = null;

	Timer timer; // timer used to send the images at the video frame rate
	byte[] buf; // buffer used to store the images to send to the client

	int state; // RTSP Server state == INIT or READY or PLAY

	final String CRLF = "\r\n";

	// --------------------------------
	// Constructor
	// --------------------------------
	public RecordServer() {

		
		buf = new byte[99000];
		
	}

	String videoName;

	private IMediaWriter getMedia() {
		// record =====================================
		DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
		videoName = cameraId.toString() + "_" + df.format(new Date()) + ".mp4";
		String fileName = "C:/Users/BinhHoc/Documents/GitHub/CAMERA-SERVER/src/main/webapp/resources/videos/"
				+ videoName;

		double FRAME_RATE = 50;

		// let's make a IMediaWriter to write the file.
		IMediaWriter writer = ToolFactory.makeWriter(fileName);

		Dimension screenBounds = Toolkit.getDefaultToolkit().getScreenSize();

		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, screenBounds.width / 2, screenBounds.height / 2);
		return writer;
	}

	// ------------------------------------
	// main
	// ------------------------------------
	@Override
	public void run() {
		try {

			long startTime = System.nanoTime();

			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

			// opencv ================================
			final Mat mat = new Mat();

			final VideoCapture videoCapture = new VideoCapture(getUrl());
			videoCapture.read(mat);
			Mat resizeMat = new Mat();
			Size sz = new Size(480, 350);
			Imgproc.resize(mat, resizeMat, sz);

			Size frameSize = new Size(resizeMat.width(), resizeMat.height());

			final Mat workImg = new Mat();
			Mat movingAvgImg = null;
			final Mat gray = new Mat();
			final Mat diffImg = new Mat();
			final Mat scaleImg = new Mat();
			final Point rectPoint1 = new Point();
			final Point rectPoint2 = new Point();
			final Scalar rectColor = new Scalar(0, 255, 0);
			final Size kSize = new Size(8, 8);
			final double totalPixels = frameSize.area();
			double motionPercent = 0.0;

			

			IMediaWriter mediaWriter = getMedia();
			while (videoCapture.read(mat)) {
				
				Mat processMat = new Mat();
				Size size = new Size(480, 350);
				Imgproc.resize(mat, processMat, size);

				// Generate work image by blurring
				Imgproc.blur(processMat, workImg, kSize);
				// Generate moving average image if needed
				if (movingAvgImg == null) {
					movingAvgImg = new Mat();
					workImg.convertTo(movingAvgImg, CvType.CV_32F);

				}
				// Generate moving average image
				Imgproc.accumulateWeighted(workImg, movingAvgImg, .03);
				// Convert the scale of the moving average
				Core.convertScaleAbs(movingAvgImg, scaleImg);
				// Subtract the work image frame from the scaled image
				// average
				Core.absdiff(workImg, scaleImg, diffImg);
				// Convert the image to grayscale
				Imgproc.cvtColor(diffImg, gray, Imgproc.COLOR_BGR2GRAY);
				// Convert to BW
				Imgproc.threshold(gray, gray, 25, 255, Imgproc.THRESH_BINARY);
				// Total number of changed motion pixels
				motionPercent = 100.0 * Core.countNonZero(gray) / totalPixels;
				// Detect if camera is adjusting and reset reference if
				// more
				// than
				// 25%
				// if (motionPercent > 50.0) {
				workImg.convertTo(movingAvgImg, CvType.CV_32F);
				// }
				final List<Rect> movementLocations = contours(gray);
				// Threshold trigger motion
				// if (motionPercent > 5) {
				for (final Rect rect : movementLocations) {
					if (rect.height > objectHeight && rect.width > objectWith) {
						rectPoint1.x = rect.x;
						rectPoint1.y = rect.y;
						rectPoint2.x = rect.x + rect.width;
						rectPoint2.y = rect.y + rect.height;
						// Draw rectangle around fond object
						Core.rectangle(processMat, rectPoint1, rectPoint2, rectColor, 2);
					}
				}

				try {

					if (stop == false) {
						// System.out.println("saving");
						BufferedImage screen = recordMatToBufferedImage(processMat);
						BufferedImage bgrScreen = convertToType(screen, 5);
						mediaWriter.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
					} else {
						// if (mediaWriter != null) {
						System.out.println("close");
						mediaWriter.close();

						Camera camera = cameraRepo.findByCameraid(cameraId);
						String result = "http://localhost:8080/videos/" + videoName;
						Video video = new Video();
						video.setCamera(camera);
						video.setDate(new Date());
						video.setVideoUrl(result);
						videoRepo.save(video);

						return;
					}

				} catch (Exception ex) {
					// mediaWriter.close();
					return;
				}

			}

			mediaWriter.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
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

	private BufferedImage recordMatToBufferedImage(Mat frame) {
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

	public void start() {
		System.out.println("Starting opencv ");
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
}
