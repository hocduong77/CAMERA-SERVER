
import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.swing.Timer;

public class camera extends Applet {

	Socket RTSPsocket; // socket used to send/receive RTSP messages
	DataInputStream RTSPBufferedReader;
	BufferedWriter RTSPBufferedWriter;

	Timer timer; // timer used to receive data from the UDP socket
	byte[] buf; // buffer used to store data received from the server
	Image image;
	// RTSP variables
	// ----------------

	final String CRLF = "\r\n";

	int width;
	int height;
	int port;
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
		width = Integer.parseInt(getParameter("width"));
		height = Integer.parseInt(getParameter("height"));
		port = Integer.parseInt(getParameter("rtpPort"));
	}

	public void paint(Graphics g) {

		g.drawImage(paintImage, 0, 0, this);
		/* g.drawImage(resize(paintImage, 480, 340), 0, 0, this); */

	}

	public BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static void main(String[] args) {
		new camera();
	}

	@Override
	public void start() {
		// Init non-blocking RTPsocket that will be used to receive data
		try {

			RTSPsocket = new Socket("localhost", port);

			// RTSPsocket = new Socket("localhost", 6699);
			// Set input and output stream filters:
			RTSPBufferedReader = new DataInputStream(RTSPsocket.getInputStream());

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

		public byte[] decompress(byte[] data) throws IOException, DataFormatException {
			Inflater inflater = new Inflater();
			inflater.setInput(data);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
			byte[] buffer = new byte[1024];
			while (!inflater.finished()) {
				int count = inflater.inflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			outputStream.close();
			byte[] output = outputStream.toByteArray();

			return output;
		}

		public void actionPerformed(ActionEvent e) {

			try {
				// receive the DP from the socket:
				int length = RTSPBufferedReader.readInt();
				byte[] message = new byte[length];// read length of incoming
													// message
				if (length > 0) {
					RTSPBufferedReader.readFully(message, 0, message.length); // read
																				// the
																				// message
				}
				byte[] decompress = null;
				try {
					decompress = decompress(message);
				} catch (DataFormatException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// create an RTPpacket object from the DP
				RTPpacket rtp_packet = new RTPpacket(decompress, decompress.length);

				// get the payload bitstream from the RTPpacket object
				int payload_length = rtp_packet.getpayload_length();
				byte[] payload = new byte[payload_length];
				rtp_packet.getpayload(payload);

				// get an Image object from the payload bitstream
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				// image = toolkit.createImage(payload, 0, payload_length);

				// paintImage =
				// ImageUtil.toBufferedImage(toolkit.createImage(payload, 0,
				// payload_length));
				paintImage = resize(ImageUtil.toBufferedImage(toolkit.createImage(payload, 0, payload_length)), width,
						height);
				repaint();
			} catch (InterruptedIOException iioe) {
				System.out.println("Nothing to read");
			} catch (IOException ioe) {
				System.out.println("Exception caught: " + ioe);
			}
		}
	}

}