package school.camera.persistence.service;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.dao.IGatewayRepo;
import school.camera.persistence.dao.IImageRepo;
import school.camera.persistence.dao.INotificationRepo;
import school.camera.persistence.dao.IVideoRepo;
import school.camera.persistence.dao.ScheduleRepo;
import school.camera.persistence.dao.UserRepository;
import school.camera.persistence.model.CamearSchedule;
import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Image;
import school.camera.persistence.model.Video;

@Service
@Transactional
public class CameraService implements ICameraService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired 
	IGatewayRepo gatewayRepo;
	
	@Autowired
	private CameraRepo cameraRepo;

	@Autowired
	private ScheduleRepo scheduleRepo;

	@Autowired
	private IImageRepo imageRepo;

	@Autowired
	private IVideoRepo videoRepo;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private INotificationRepo notificationRepo;

	@Autowired
	private UserRepository userRepo;

	@Override
	public String captureNew(Long cameraId) throws IOException, InterruptedException {
		Camera camera = cameraRepo.findByCameraid(cameraId);
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		// capture t = new capture();
		VideoCapture videoCapture = new VideoCapture(camera.getCameraUrl());

		DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
		String imageName = df.format(new Date());
		String fileName = "C:/Users/BinhHoc/Documents/GitHub/CAMERA-SERVER/src/main/webapp/resources/images/"
				+ imageName + ".png";

		Mat frame = new Mat();
		videoCapture.read(frame);

		if (!videoCapture.isOpened()) {
			LOGGER.error("camera can not open");
		} else {
			while (true) {

				if (videoCapture.read(frame)) {

					BufferedImage image = MatToBufferedImage(frame);
					saveImage(image, fileName);
					break;
				}
			}
		}
		videoCapture.release();
		String result = "http://localhost:8080/images/" + imageName + ".png";
		Image image = new Image();
		image.setCamera(camera);
		image.setDate(new Date());
		image.setImageUrl(result);
		imageRepo.save(image);
		LOGGER.info(">>>>>> END CAPTURE >>>>>>");
		return result;
	}

	// Save an image
	public static void saveImage(BufferedImage img, String fileName) {
		try {
			File outputfile = new File(fileName);
			ImageIO.write(img, "png", outputfile);
		} catch (Exception e) {
			System.out.println("error");
		}
	}

	public BufferedImage MatToBufferedImage(Mat frame) {
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

	@Override
	public String recordNew(Long cameraId) throws IOException, InterruptedException {
		LOGGER.info(">>>>>> START RECORD >>>>>>");
		Camera camera = cameraRepo.findByCameraid(cameraId);

		CamearSchedule schedule = scheduleRepo.findBycamera(camera);

		double FRAME_RATE = 50;
		int SECONDS_TO_RUN_FOR = (int) ((schedule.getRecordSchedule().getTime() - schedule.getRecordTime().getTime())
				/ 1000);
		LOGGER.info(">>>>>>SECONDS_TO_RUN_FOR >>>>>> {}", SECONDS_TO_RUN_FOR);
		Server2 streamingServer = new Server2();
		streamingServer.setUrl(camera.getCameraUrl());
		streamingServer.setRTP_dest_port(camera.getPort().getPort());
		streamingServer.cameraId = camera.getCameraid();
		
		streamingServer.objectWith = camera.getObjectWith();
		streamingServer.objectHeight = camera.getObjectHeight();
		streamingServer.cameraRepo = cameraRepo;
		streamingServer.gatewayRepo = gatewayRepo;
		streamingServer.videoRepo = videoRepo;
		streamingServer.imageRepo = imageRepo;
		streamingServer.notificationRepo = notificationRepo;
		streamingServer.mailSender = mailSender;
		streamingServer.userRepo = userRepo;
		streamingServer.startStop = true;
		streamingServer.isScheduler = true;
		streamingServer.schedulerTime = (int) (SECONDS_TO_RUN_FOR * FRAME_RATE);
		streamingServer.start();

		LOGGER.info(">>>>>> END RECORD >>>>>>");
		return null;
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

}
