
import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.Timer;

public class camera extends Applet {

	// RTP variables:
	// ----------------
	DatagramPacket rcvdp; // UDP packet received from the server
	DatagramSocket RTPsocket; // socket to be used to send and receive
								// UDP

	Timer timer; // timer used to receive data from the UDP socket
	byte[] buf; // buffer used to store data received from the server
	Image image;
	// RTSP variables
	// ----------------


	final String CRLF = "\r\n";

	/**
	 * image paint to applet.
	 */
	BufferedImage paintImage;

	@Override
	public void init() {

		// --------------------------
		timer = new Timer(1, new timerListener());
		timer.setInitialDelay(0);
		timer.setCoalesce(true);

		// allocate enough memory for the buffer used to receive data from the
		// server
		buf = new byte[99000];

	}

	public void paint(Graphics g) {

		// g.drawImage(image, 0, 0, this);
		g.drawImage(paintImage, 0, 0, this);
	}
	public static void main(String[] args) {
		new camera();
	}
	@Override
	public void start() {
		// Init non-blocking RTPsocket that will be used to receive data
		try {
			RTPsocket = new DatagramSocket( Integer.parseInt(getParameter("rtpPort")));
			// set TimeOut value of the socket to 5msec.
			RTPsocket.setSoTimeout(5000);
			// set TimeOut value of the socket to 5msec.

		} catch (SocketException se) {
			System.out.println("Socket exception: " + se);
			System.exit(0);
		}
		System.out.println("New RTSP state: PLAYING");
		// start the timer
		timer.start();

	}

	class timerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// Construct a DatagramPacket to receive data from the UDP socket

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

}