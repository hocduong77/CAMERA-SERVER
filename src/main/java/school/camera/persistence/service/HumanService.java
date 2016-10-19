package school.camera.persistence.service;

/*
 * buildpath -> library -> add user library.
 * 
 */

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

/**
 * Uses moving average to determine change percent.
 *
 * args[0] = source file or will default to "../resources/traffic.mp4" if no
 * args passed.
 *
 * @author sgoldsmith
 * @version 1.0.0
 * @since 1.0.0
 */
@SuppressWarnings({ "checkstyle:magicnumber", "PMD.LawOfDemeter", "PMD.AvoidLiteralsInIfCondition",
        "PMD.AvoidInstantiatingObjectsInLoops", "PMD.AvoidUsingNativeCode", "PMD.AvoidFinalLocalVariable",
        "PMD.CommentSize", "PMD.AvoidPrintStackTrace", "PMD.UseProperClassLoader", "PMD.AvoidPrefixingMethodParameters",
        "PMD.DataflowAnomalyAnalysis" })
public class HumanService {
    /**
     * Logger.
     */
    // Logger is not a constant
    @SuppressWarnings({ "checkstyle:constantname", "PMD.VariableNamingConventions" })
    private static final Logger logger = Logger.getLogger(HumanService.class.getName());
    /* Load the OpenCV system library */
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
    private HumanService() {
        throw new AssertionError();
    }

    /**
     * Get contours from image.
     *
     * @param source
     *            Source image.
     * @return List of rectangles.
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

    /**
     * Mark frames with motion detected.
     *
     * args[0] = source file or will default to "../resources/traffic.mp4" if no
     * args passed.
     *
     * @param args
     *            String array of arguments.
     */
    public static void main(String[] args) {
    	
    	JFrame jframe = new JFrame("HUMAN MOTION DETECTOR FPS");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel vidpanel = new JLabel();
		jframe.setContentPane(vidpanel);
		jframe.setSize(640, 480);
		jframe.setVisible(true);
		
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
        final long startTime = System.currentTimeMillis();
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
            // Subtract the work image frame from the scaled image average
            Core.absdiff(workImg, scaleImg, diffImg);
            // Convert the image to grayscale
            Imgproc.cvtColor(diffImg, gray, Imgproc.COLOR_BGR2GRAY);
            // Convert to BW
            Imgproc.threshold(gray, gray, 25, 255, Imgproc.THRESH_BINARY);
            // Total number of changed motion pixels
            motionPercent = 100.0 * Core.countNonZero(gray) / totalPixels;
            // Detect if camera is adjusting and reset reference if more than
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
            

			ImageIcon image = new ImageIcon(Mat2bufferedImage(mat));
			vidpanel.setIcon(image);
			vidpanel.repaint();
			
            frames++;
        }
        final long estimatedTime = System.currentTimeMillis() - startTime;
        final double seconds = (double) estimatedTime / 1000;
       
   
    }
    
	public static BufferedImage Mat2bufferedImage(Mat image) {
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
}