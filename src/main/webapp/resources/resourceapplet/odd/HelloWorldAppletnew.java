
import java.applet.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.Timer;

public class HelloWorldAppletnew extends Applet {
	// GUI

	ImageIcon icon;

	// RTP variables:
	// ----------------
	DatagramPacket rcvdp; // UDP packet received from the server
	static DatagramSocket RTPsocket; // socket to be used to send and receive
										// UDP
	// packets
	static int RTP_RCV_PORT = 8207; // port where the client will receive the
									// RTP packets

	static Timer timer; // timer used to receive data from the UDP socket
	byte[] buf; // buffer used to store data received from the server
	static Image image;
	// RTSP variables
	// ----------------
	// rtsp states
	final static int INIT = 0;
	final static int READY = 1;
	final static int PLAYING = 2;
	static int state; // RTSP state == INIT or READY or PLAYING
	Socket RTSPsocket; // socket used to send/receive RTSP messages
	// input and output stream filters
	static BufferedReader RTSPBufferedReader;
	static BufferedWriter RTSPBufferedWriter;
	static String VideoFileName; // video file to request to the server
	static int RTSPSeqNb = 0; // Sequence number of RTSP messages within the
								// session
	static int RTSPid = 0; // ID of the RTSP session (given by the RTSP Server)

	final static String CRLF = "\r\n";

	// Video constants:
	// ------------------
	static int MJPEG_TYPE = 26; // RTP payload type for MJPEG video

	/**
	 * image paint to applet.
	 */
	static BufferedImage paintImage;

	@Override
	public void init() {

		// --------------------------
		timer = new Timer(1, new timerListener());
		timer.setInitialDelay(0);
		timer.setCoalesce(true);

		// allocate enough memory for the buffer used to receive data from the
		// server
		buf = new byte[15000];

	}

	public void paint(Graphics g) {

		// g.drawImage(image, 0, 0, this);
		g.drawImage(paintImage, 0, 0, this);
	}
	 public static void main(String[] args) {
	        new HelloWorldAppletnew();
	    }
	@Override
	public void start() {

		// Create a Client object
		// Client theClient = new Client();

		// get server RTSP port and IP address from the command line
		// ------------------

		// get video filename to request:
		VideoFileName = "media/movie.Mjpeg"; // argv[2];

		// Establish a TCP connection with the server to exchange RTSP messages
		// ------------------
		// theClient.RTSPsocket = new Socket(ServerIPAddr, RTSP_server_port);

		try {
			RTSPsocket = new Socket("localhost", 8205);
			// Set input and output stream filters:
			RTSPBufferedReader = new BufferedReader(new InputStreamReader(RTSPsocket.getInputStream()));
			RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(RTSPsocket.getOutputStream()));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// init RTSP state:
		state = INIT;
		// setup
		// Init non-blocking RTPsocket that will be used to receive data
		try {
			// construct a new DatagramSocket to receive RTP packets from the
			// server, on port RTP_RCV_PORT
			// RTPsocket = ...
			InetAddress addr = null;
			try {
				addr = InetAddress.getByName("localhost");
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			RTPsocket = new DatagramSocket(RTP_RCV_PORT);
			// set TimeOut value of the socket to 5msec.

			RTPsocket.setSoTimeout(5000);
			// set TimeOut value of the socket to 5msec.
			// ....

		} catch (SocketException se) {
			System.out.println("Socket exception: " + se);
			System.exit(0);
		}

		// init RTSP sequence number
		RTSPSeqNb = 1;

		// Send SETUP message to the server
		send_RTSP_request("SETUP");

		// Wait for the response
		if (parse_server_response() != 200)
			System.out.println("Invalid Server Response");
		else {
			// change RTSP state and print new state
			state = READY;
			System.out.println("New RTSP state: READY");
			// System.out.println("New RTSP state: ....");
		}
		// playyyyyyyyyyyyyyyyy

		if (state == READY) {

			// increase RTSP sequence number

			RTSPSeqNb++;

			// send PLAY message to the server

			send_RTSP_request("PLAY");

			// wait for the response

			if (parse_server_response() != 200)
				System.out.println("Invalid Server Response");
			else {

				// change RTSP state and print out new state

				state = PLAYING;
				System.out.println("New RTSP state: PLAYING");

				// start the timer

				timer.start();
			}
		}

	}

	class timerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// Construct a DatagramPacket to receive data from the UDP socket

			// DatagramPacket packet = new DatagramPacket(buf, buf.length, addr,
			// 8207);

			rcvdp = new DatagramPacket(buf, buf.length);

			try {
				// receive the DP from the socket:
				RTPsocket.receive(rcvdp);
				// create an RTPpacket object from the DP
				RTPpacket rtp_packet = new RTPpacket(rcvdp.getData(), rcvdp.getLength());

				// print important header fields of the RTP packet received:
				System.out.println("Got RTP packet with SeqNum # " + rtp_packet.getsequencenumber() + " TimeStamp "
						+ rtp_packet.gettimestamp() + " ms, of type " + rtp_packet.getpayloadtype());

				// print header bitstream:
				rtp_packet.printheader();

				// get the payload bitstream from the RTPpacket object
				int payload_length = rtp_packet.getpayload_length();
				byte[] payload = new byte[payload_length];
				rtp_packet.getpayload(payload);

				// get an Image object from the payload bitstream
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				// image = toolkit.createImage(payload, 0, payload_length);

				paintImage = ImageUtil.toBufferedImage(toolkit.createImage(payload, 0, payload_length));

				repaint();
			} catch (InterruptedIOException iioe) {
				// System.out.println("Nothing to read");
			} catch (IOException ioe) {
				System.out.println("Exception caught: " + ioe);
			}
		}
	}

	// ------------------------------------
	// Parse Server Response
	// ------------------------------------
	private static int parse_server_response() {
		int reply_code = 0;

		try {
			// parse status line and extract the reply_code:
			String StatusLine = RTSPBufferedReader.readLine();
			// System.out.println("RTSP Client - Received from Server:");
			System.out.println(StatusLine);

			StringTokenizer tokens = new StringTokenizer(StatusLine);
			tokens.nextToken(); // skip over the RTSP version
			reply_code = Integer.parseInt(tokens.nextToken());

			// if reply code is OK get and print the 2 other lines
			if (reply_code == 200) {
				String SeqNumLine = RTSPBufferedReader.readLine();
				System.out.println(SeqNumLine);

				String SessionLine = RTSPBufferedReader.readLine();
				System.out.println(SessionLine);

				// if state == INIT gets the Session Id from the SessionLine
				tokens = new StringTokenizer(SessionLine);
				tokens.nextToken(); // skip over the Session:
				RTSPid = Integer.parseInt(tokens.nextToken());
			}
		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex);
			System.exit(0);
		}

		return (reply_code);
	}

	// ------------------------------------
	// Send RTSP Request
	// ------------------------------------

	// .............
	// TO COMPLETE
	// .............

	private static void send_RTSP_request(String request_type) {
		try {

			RTSPBufferedWriter.write(request_type + " " + VideoFileName + " " + "RTSP/1.0" + CRLF);

			RTSPBufferedWriter.write("CSeq: " + RTSPSeqNb + CRLF);

			if (request_type.equals("SETUP")) {
				RTSPBufferedWriter.write("Transport: RTP/UDP; client_port= " + RTP_RCV_PORT + CRLF);
			}

			else {
				RTSPBufferedWriter.write("Session: " + RTSPid + CRLF);
			}

			RTSPBufferedWriter.flush();
		} catch (Exception ex) {
			System.out.println("Exception caught: " + ex);
		}
	}

}