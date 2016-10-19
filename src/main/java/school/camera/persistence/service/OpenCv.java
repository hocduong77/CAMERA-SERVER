package school.camera.persistence.service;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;
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
public class OpenCv implements Runnable {
	private Thread thread;
	private String cameraUrl;
	private int RTP_dest_port;
	private String threadName;

	public OpenCv(String name) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		// allocate memory for the sending buffer
		buf = new byte[99000];

		// GUI:
		label = new JLabel("Send frame #        ", JLabel.CENTER);
		threadName = name;
		System.out.println("Creating opencv");
	}

	public static BufferedImage Mat2bufferedImage(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		System.out.println("opencv bytes" + bytes.length);
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

	public static int Mat2bufferedByte(Mat image) {
		MatOfByte bytemat = new MatOfByte();
		Highgui.imencode(".jpg", image, bytemat);
		byte[] bytes = bytemat.toArray();
		System.out.println("opencv bytes" + bytes.length);
		for (int i = 0; i < bytes.length; i++) {
			buf[i] = bytes[i];
		}
		return bytes.length;
	}

	// ------------------------------------
	// Parse RTSP Request
	// ------------------------------------
	int parse_RTSP_request() {
		int request_type = -1;
		try {
			// parse request line and extract the request_type:
			String RequestLine = RTSPBufferedReader.readLine();
			// System.out.println("RTSP Server - Received from Client:");
			System.out.println(RequestLine);

			StringTokenizer tokens = new StringTokenizer(RequestLine);
			String request_type_string = tokens.nextToken();

			// convert to request_type structure:
			if ((new String(request_type_string)).compareTo("SETUP") == 0)
				request_type = SETUP;
			else if ((new String(request_type_string)).compareTo("PLAY") == 0)
				request_type = PLAY;
			else if ((new String(request_type_string)).compareTo("PAUSE") == 0)
				request_type = PAUSE;
			else if ((new String(request_type_string)).compareTo("TEARDOWN") == 0)
				request_type = TEARDOWN;

			if (request_type == SETUP) {
				// extract VideoFileName from RequestLine
				VideoFileName = tokens.nextToken();
			}

			// parse the SeqNumLine and extract CSeq field
			String SeqNumLine = RTSPBufferedReader.readLine();
			System.out.println(SeqNumLine);
			tokens = new StringTokenizer(SeqNumLine);
			tokens.nextToken();
			RTSPSeqNb = Integer.parseInt(tokens.nextToken());

			// get LastLine
			String LastLine = RTSPBufferedReader.readLine();
			System.out.println(LastLine);

			if (request_type == SETUP) {

				// extract RTP_dest_port from LastLine
				tokens = new StringTokenizer(LastLine);
				for (int i = 0; i < 3; i++)
					tokens.nextToken(); // skip unused stuff 8205
				RTP_dest_port = Integer.parseInt(tokens.nextToken());
				// RTP_dest_port = 8207;
			}
			// else LastLine will be the SessionId line ... do not check for
			// now.
		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex);
			System.exit(0);
		}
		return (request_type);
	}

	// ------------------------------------
	// Send RTSP Response
	// ------------------------------------
	private static void send_RTSP_response() {
		try {
			RTSPBufferedWriter.write("RTSP/1.0 200 OK" + CRLF);
			RTSPBufferedWriter.write("CSeq: " + RTSPSeqNb + CRLF);
			RTSPBufferedWriter.write("Session: " + RTSP_ID + CRLF);
			RTSPBufferedWriter.flush();
			// System.out.println("RTSP Server - Sent response to Client.");
		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex);
			System.exit(0);
		}
	}

	@Override
	public void run() {
		try {

			String url = null;

			final Mat mat = new Mat();

			final VideoCapture videoCapture = new VideoCapture("rtsp://192.168.1.100:554/live.sdp");
			videoCapture.read(mat);
			Size frameSize = new Size(mat.width(), mat.height());

			int frames = 0;
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
			int framesWithMotion = 0;

			// create a Server object

			// show GUI:
			// theServer.pack();
			// setVisible(true);

			// get RTSP socket port from the command line
			int RTSPport = 4499;// Integer.parseInt(argv[0]);

			// Initiate TCP connection with the client for the RTSP session
			ServerSocket listenSocket = new ServerSocket(RTSPport);
			RTSPsocket = listenSocket.accept();
			listenSocket.close();

			// Get Client IP address
			ClientIPAddr = RTSPsocket.getInetAddress();

			// Initiate RTSPstate
			state = INIT;

			// Set input and output stream filters:
			RTSPBufferedReader = new BufferedReader(new InputStreamReader(RTSPsocket.getInputStream()));
			RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(RTSPsocket.getOutputStream()));

			// Wait for the SETUP message from the client
			int request_type;
			boolean done = false;
			while (!done) {
				request_type = parse_RTSP_request(); // blocking

				if (request_type == SETUP) {
					done = true;

					// update RTSP state
					state = READY;
					System.out.println("New RTSP state: READY");

					// Send response
					send_RTSP_response();

					// init the VideoStream object:
					// theServer.video = new VideoStream(VideoFileName);

					// init RTP socket
					RTPsocket = new DatagramSocket();
				}
			}

			// loop to handle RTSP requests
			while (true) {
				// parse the request
				request_type = parse_RTSP_request(); // blocking

				if ((request_type == PLAY) && (state == READY)) {
					// send back response
					send_RTSP_response();
					// start timer
					// theServer.timer.start();
					// update state
					state = PLAYING;
					System.out.println("New RTSP state: PLAYING");

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
							framesWithMotion++;
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
							System.out.print("\n" + image_length);
							System.out.println("image_length " + image_length + "buff " + buf.length);
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
							senddp = new DatagramPacket(packet_bits, packet_length, ClientIPAddr, RTP_dest_port);
							System.out.print("RTP_dest_port =" + RTP_dest_port + "\n");

							RTPsocket.send(senddp);
							// RTSPBufferedWriter.wri
							// System.out.println("Send frame #"+imagenb);
							// print the header bitstream
							rtp_packet.printheader();

							// update GUI
							label.setText("Send frame #" + imagenb);
						} catch (Exception ex) {
							System.out.println("Exception caught: " + ex);
							System.exit(0);
						}

						frames++;
					}

				} else if ((request_type == PAUSE) && (state == PLAYING)) {
					// send back response
					send_RTSP_response();
					// stop timer
					// theServer.timer.stop();
					// update state
					state = READY;
					System.out.println("New RTSP state: READY");
				} else if (request_type == TEARDOWN) {
					// send back response
					send_RTSP_response();
					// stop timer
					// theServer.timer.stop();
					// close sockets
					RTSPsocket.close();
					RTPsocket.close();

					System.exit(0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * Kernel used for contours.
	 */
	private static final Mat CONTOUR_KERNEL = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3, 3),
			new Point(1, 1));
	/**
	 * Contour hierarchy.
	 */
	private static final Mat HIERARCHY = new Mat();
	/**
	 * Point used for contour dilate and erode.
	 */
	private static final Point CONTOUR_POINT = new Point(-1, -1);

	/**
	 * Suppress default constructor for noninstantiability.
	 */
	public static List<Rect> contours(final Mat source) {
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
	static DatagramSocket RTPsocket; // socket to be used to send and receive
										// UDP
	// packets
	static DatagramPacket senddp; // UDP packet containing the video frames

	static InetAddress ClientIPAddr; // Client IP address
										// the
	// RTSP Client)

	// GUI:
	// ----------------
	static JLabel label;

	// Video variables:
	// ----------------
	static int imagenb = 0; // image nb of the image currently transmitted
	VideoStream video; // VideoStream object used to access video frames
	static int MJPEG_TYPE = 26; // RTP payload type for MJPEG video
	static int FRAME_PERIOD = 100; // Frame period of the video to stream, in ms
	static int VIDEO_LENGTH = 500; // length of the video in frames

	Timer timer; // timer used to send the images at the video frame rate
	static byte[] buf; // buffer used to store the images to send to the client

	// RTSP variables
	// ----------------
	// rtsp states
	final static int INIT = 0;
	final static int READY = 1;
	final static int PLAYING = 2;
	// rtsp message types
	final static int SETUP = 3;
	final static int PLAY = 4;
	final static int PAUSE = 5;
	final static int TEARDOWN = 6;

	static int state; // RTSP Server state == INIT or READY or PLAY
	Socket RTSPsocket; // socket used to send/receive RTSP messages
	// input and output stream filters
	static BufferedReader RTSPBufferedReader;
	static BufferedWriter RTSPBufferedWriter;
	static String VideoFileName; // video file requested from the client
	static int RTSP_ID = 123456; // ID of the RTSP session
	static int RTSPSeqNb = 0; // Sequence number of RTSP messages within the
								// session

	final static String CRLF = "\r\n";

	public String getCameraUrl() {
		return cameraUrl;
	}

	public void setCameraUrl(String cameraUrl) {
		this.cameraUrl = cameraUrl;
	}

	public int getRTP_dest_port() {
		return RTP_dest_port;
	}

	public void setRTP_dest_port(int rTP_dest_port) {
		RTP_dest_port = rTP_dest_port;
	}

	public void start() {
		System.out.println("Starting opencv ");
		if (thread == null) {
			thread = new Thread(this, threadName);
		}
	}
}
