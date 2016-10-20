package school.camera.persistence.service;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
/* ------------------
   Server
   usage: java Server [RTSP listening port]
   ---------------------- */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

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

@SuppressWarnings({ "checkstyle:magicnumber", "PMD.LawOfDemeter", "PMD.AvoidLiteralsInIfCondition",
		"PMD.AvoidInstantiatingObjectsInLoops", "PMD.AvoidUsingNativeCode", "PMD.AvoidFinalLocalVariable",
		"PMD.CommentSize", "PMD.AvoidPrintStackTrace", "PMD.UseProperClassLoader", "PMD.AvoidPrefixingMethodParameters",
		"PMD.DataflowAnomalyAnalysis" })
public class Server2 extends JFrame implements Runnable {
	private Thread thread;
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

	Timer timer; // timer used to send the images at the video frame rate
	byte[] buf; // buffer used to store the images to send to the client

	int state; // RTSP Server state == INIT or READY or PLAY

	final String CRLF = "\r\n";

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

	// ------------------------------------
	// main
	// ------------------------------------
	@Override
	public void run() {
		try {

			final Mat mat = new Mat();

			final VideoCapture videoCapture = new VideoCapture(getUrl());
			videoCapture.read(mat);
			Size frameSize = new Size(mat.width(), mat.height());

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

			while (videoCapture.read(mat)) {
				// Generate work image by blurring
				Imgproc.blur(mat, workImg, kSize);
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
				if (motionPercent > 50.0) {
					workImg.convertTo(movingAvgImg, CvType.CV_32F);
				}
				final List<Rect> movementLocations = contours(gray);
				// Threshold trigger motion
				if (motionPercent > 5) {
					for (final Rect rect : movementLocations) {
						rectPoint1.x = rect.x;
						rectPoint1.y = rect.y;
						rectPoint2.x = rect.x + rect.width;
						rectPoint2.y = rect.y + rect.height;
						// Draw rectangle around fond object
						Core.rectangle(mat, rectPoint1, rectPoint2, rectColor, 2);
					}
				}

				try {
					// get next frame to send from the video, as well as
					// its
					// size
					int image_length = Mat2bufferedByte(mat);
					// System.out.print("\n" + image_length);
					// System.out.println("image_length " + image_length + "buff
					// " + buf.length);
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
					// System.out.print("RTP_dest_port =" + getRTP_dest_port() +
					// "\n");

					RTPsocket.send(senddp);
					// RTSPBufferedWriter.wri
					// System.out.println("Send frame #"+imagenb);
					// print the header bitstream
					// rtp_packet.printheader();

					// update GUI
					label.setText("Send frame #" + imagenb);
				} catch (Exception ex) {
					// System.out.println("Exception caught: " + ex);
					System.exit(0);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public BufferedImage Mat2bufferedImage(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		// System.out.println("opencv bytes" + bytes.length);
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
		// System.out.println("opencv bytes" + bytes.length);
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
