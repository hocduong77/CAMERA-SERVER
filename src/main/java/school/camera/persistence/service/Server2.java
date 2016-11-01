package school.camera.persistence.service;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.Timer;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.dao.IImageRepo;
import school.camera.persistence.dao.IVideoRepo;
import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Video;

@SuppressWarnings({ "checkstyle:magicnumber", "PMD.LawOfDemeter", "PMD.AvoidLiteralsInIfCondition",
		"PMD.AvoidInstantiatingObjectsInLoops", "PMD.AvoidUsingNativeCode", "PMD.AvoidFinalLocalVariable",
		"PMD.CommentSize", "PMD.AvoidPrintStackTrace", "PMD.UseProperClassLoader", "PMD.AvoidPrefixingMethodParameters",
		"PMD.DataflowAnomalyAnalysis" })
public class Server2 extends JFrame implements Runnable {

	public IVideoRepo videoRepo;

	public CameraRepo cameraRepo;
	public IImageRepo imageRepo;
	public Thread thread;
	private RecordServer recordServer;
	// opencv
	/**
	 * Logger.
	 */
	// Logger is not a constant
	// @SuppressWarnings({ "checkstyle:constantname",
	// "PMD.VariableNamingConventions" })
	// private static final Logger logger =
	// Logger.getLogger(human.class.getName());
	/* Load the OpenCV system library */
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

	// RTP variables:
	// ----------------
	DatagramSocket RTPsocket; // socket to be used to send and receive
								// UDP
	// packets
	DatagramPacket senddp; // UDP packet containing the video frames

	InetAddress ClientIPAddr; // Client IP address
	int RTP_dest_port = 0; // destination port for RTP packets (given by
	String url; // String url = "rtsp://192.168.1.100:554/live.sdp";

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getRTP_dest_port() {
		return RTP_dest_port;
	}

	public void setRTP_dest_port(int rTP_dest_port) {
		RTP_dest_port = rTP_dest_port;
	}

	// GUI:
	// ----------------
	JLabel label;

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

	Date captureTime = null;

	boolean isDetected = false;

	Timer timer; // timer used to send the images at the video frame rate
	byte[] buf; // buffer used to store the images to send to the client

	int state; // RTSP Server state == INIT or READY or PLAY

	final String CRLF = "\r\n";

	private IMediaWriter mediaWriter;
	private String videoName;

	private long startTime = 0;

	// --------------------------------
	// Constructor
	// --------------------------------
	public Server2() {

		// init Frame
		super("Server");
		//
		// // init Timer
		// timer = new Timer(FRAME_PERIOD, this);
		// timer.setInitialDelay(0);
		// timer.setCoalesce(true);

		// allocate memory for the sending buffer
		buf = new byte[99000];

		// Handler to close the main window
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// stop the timer and exit
				timer.stop();
				System.exit(0);
			}
		});

		// GUI:
		label = new JLabel("Send frame #        ", JLabel.CENTER);
		getContentPane().add(label, BorderLayout.CENTER);
	}

	private IMediaWriter getMedia() {
		// record =====================================
		DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
		this.videoName = cameraId.toString() + "_" + df.format(new Date()) + ".mp4";
		String fileName = "C:/Users/BinhHoc/Documents/GitHub/CAMERA-SERVER/src/main/webapp/resources/videos/"
				+ videoName;

		double FRAME_RATE = 50;

		// let's make a IMediaWriter to write the file.
		IMediaWriter writer = ToolFactory.makeWriter(fileName);

		Dimension screenBounds = Toolkit.getDefaultToolkit().getScreenSize();

		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, screenBounds.width / 2, screenBounds.height / 2);
		return writer;
	}

	@Override
	public void run() {
		try {

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

			// create a Server object
			// Server2 theServer = new Server2();

			// show GUI:
			pack();
			setVisible(true);

			// Get Client IP address
			ClientIPAddr = InetAddress.getByName("localhost");
			RTPsocket = new DatagramSocket();
			boolean recordBefore = false;
			while (videoCapture.read(mat)) {

				// System.out.println(
				// "cameraId " + cameraId + " objectWith " + objectWith + "
				// objectHeight " + objectHeight);
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
				isDetected = false;
				for (final Rect rect : movementLocations) {

					if (rect.height > objectHeight && rect.width > objectWith) {
						isDetected = true;
						rectPoint1.x = rect.x;
						rectPoint1.y = rect.y;
						rectPoint2.x = rect.x + rect.width;
						rectPoint2.y = rect.y + rect.height;
						// Draw rectangle around fond object
						Core.rectangle(processMat, rectPoint1, rectPoint2, rectColor, 2);
					}
					if (isDetected) {
						recordTime = new Date();

					}
				}

				// if (isCapture()) {
				// faceDetector(mat);
				// }
				// check save or not.
				// save image
				// System.out.println(isDetected + "/" + isSaving(recordTime));
				if (isDetected == true && recordBefore == false) {
					/*
					 * DateFormat df = new
					 * SimpleDateFormat("MM_dd_yyyy_HH_mm_ss"); String videoName
					 * = df.format(new Date()); System.out.println(
					 * "start record " + videoName); recordServer = new
					 * RecordServer(); recordServer.setUrl(getUrl());
					 * recordServer.stop = false; System.out.println("cameraId "
					 * + cameraId); recordServer.cameraId = this.cameraId;
					 * recordServer.cameraRepo = this.cameraRepo;
					 * recordServer.videoRepo = this.videoRepo;
					 * recordServer.objectWith = this.objectWith;
					 * recordServer.objectHeight = this.objectHeight;
					 * recordServer.start();
					 */
					System.out.println("start record");
					this.startTime = System.nanoTime();
					this.mediaWriter = getMedia();
					recordBefore = true;
				}
				if (isSaving(recordTime) == true) {
					BufferedImage screen = recordMatToBufferedImage(processMat);
					BufferedImage bgrScreen = convertToType(screen, 5);
					this.mediaWriter.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
				}
				if (recordBefore == true && isSaving(recordTime) == false) {
					/*
					 * DateFormat df = new
					 * SimpleDateFormat("MM_dd_yyyy_HH_mm_ss"); String videoName
					 * = df.format(new Date()); System.out.println(
					 * "stop record " + videoName); recordServer.stop = true;
					 */
					this.mediaWriter.close();
					this.mediaWriter.flush();
					this.mediaWriter = null;
					recordTime = null;
					recordBefore = false;

					Camera camera = cameraRepo.findByCameraid(cameraId);
					String result = "http://localhost:8080/videos/" + this.videoName;
					Video video = new Video();
					video.setCamera(camera);
					video.setDate(new Date());
					video.setVideoUrl(result);
					videoRepo.save(video);
					System.out.println("stop record");
				}

				try {

					int image_length = Mat2bufferedByte(processMat);
					// Builds an RTPpacket object containing the frame
					RTPpacket rtp_packet = new RTPpacket(MJPEG_TYPE, imagenb, imagenb * FRAME_PERIOD, buf,
							image_length);

					// get to total length of the full rtp packet to
					// send
					int packet_length = rtp_packet.getlength();

					// retrieve the packet bitstream and store it in an
					// array of
					// bytes
					byte[] packet_bits = new byte[packet_length];
					rtp_packet.getpacket(packet_bits);

					// send the packet as a DatagramPacket over the UDP
					// socket
					if (packet_bits.length > 65507) {
						continue;
					}
					senddp = new DatagramPacket(packet_bits, packet_length, ClientIPAddr, getRTP_dest_port());

					RTPsocket.send(senddp);
					// update GUI
					label.setText("Send frame #" + imagenb);
				} catch (Exception ex) {
					System.exit(0);
				}

			}

			// mediaWriter.close();

		} catch (

		Exception e) {
			e.printStackTrace();
		}

	}

	// private void faceDetector(Mat frame) {
	//
	// CascadeClassifier faceDetector = new
	// CascadeClassifier("D:/DownLoad/haarcascade_frontalface_alt.xml");
	//
	// MatOfRect faceDetections = new MatOfRect();
	// faceDetector.detectMultiScale(frame, faceDetections);
	//
	// System.out.println(String.format("Detected %s faces",
	// faceDetections.toArray().length));
	// if (faceDetections.toArray().length >= 1) {
	// this.captureTime = new Date();
	// for (Rect rect : faceDetections.toArray()) {
	// Core.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x +
	// rect.width, rect.y + rect.height),
	// new Scalar(0, 255, 0));
	// }
	// DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
	// String imageName = cameraId.toString() + "_" + df.format(new Date()) +
	// ".png";
	// String fileName =
	// "C:/Users/BinhHoc/Documents/GitHub/CAMERA-SERVER/src/main/webapp/resources/images/"
	// + imageName ;
	// System.out.println(String.format("Writing %s", fileName));
	// Highgui.imwrite(fileName, frame);
	// Camera camera = cameraRepo.findByCameraid(cameraId);
	// Image image = new Image();
	// image.setCamera(camera);
	// image.setDate(new Date());
	// String result = "http://localhost:8080/images/" + imageName ;
	// image.setImageUrl(result);
	// imageRepo.save(image);
	// } else {
	// return;
	// }
	//
	// }

//	private boolean isCapture() {
//		if (this.captureTime == null) {
//			return true;
//		}
//		Date d2 = new Date();
//		long seconds = (d2.getTime() - this.captureTime.getTime()) / 1000;
//		// System.out.println(" diff time = " + seconds);
//		if (seconds > 10) {
//
//			return true;
//		} else {
//			return false;
//		}
//
//	}

	private boolean isSaving(Date recordStart) {
		if (recordStart == null) {
			// System.out.println("recordStart = null");
			return false;
		}
		Date d2 = new Date();
		long seconds = (d2.getTime() - recordStart.getTime()) / 1000;
		// System.out.println(" diff time = " + seconds);
		if (seconds < 10) {
			return true;
		} else {
			return false;
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

	public BufferedImage Mat2bufferedImage(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		InputStream in = new ByteArrayInputStream(bytes);
		BufferedImage img = null;
		try {
			img = ImageIO.read(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
	}

	public int Mat2bufferedByte(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		for (int i = 0; i < bytes.length; i++) {
			buf[i] = bytes[i];
		}
		return bytes.length;
	}

	public void start() {
		System.out.println("Starting opencv ");
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}
	
	
	
}
