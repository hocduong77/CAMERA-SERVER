
import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.Timer;

public class camera extends Applet {



	Socket RTSPsocket; // socket used to send/receive RTSP messages
	DataInputStream  RTSPBufferedReader;
	BufferedWriter RTSPBufferedWriter;
	
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
			
			RTSPsocket = new Socket("localhost", Integer.parseInt(getParameter("rtpPort")));
			//RTSPsocket = new Socket("localhost", 4499);
			// Set input and output stream filters:
			RTSPBufferedReader = new DataInputStream (RTSPsocket.getInputStream());

		} catch (SocketException se) {
			System.out.println("Socket exception: " + se);
			System.exit(0);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// start the timer
		timer.start();

	}

	class timerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {



			try {
				// receive the DP from the socket:
				int length = RTSPBufferedReader.readInt();  
				byte[] message = new byte[length];// read length of incoming message
				if(length>0) {				    
				    RTSPBufferedReader.readFully(message, 0, message.length); // read the message
				}
				

				// create an RTPpacket object from the DP
				RTPpacket rtp_packet = new RTPpacket(message,length);

				

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
				 System.out.println("Nothing to read");
			} catch (IOException ioe) {
				System.out.println("Exception caught: " + ioe);
			}
		}
	}

}