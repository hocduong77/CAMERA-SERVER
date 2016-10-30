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
import org.springframework.stereotype.Service;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.dao.IImageRepo;
import school.camera.persistence.dao.IVideoRepo;
import school.camera.persistence.dao.ScheduleRepo;
import school.camera.persistence.model.CamearSchedule;
import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Image;
import school.camera.persistence.model.Video;

@Service
@Transactional
public class CameraService implements ICameraService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private CameraRepo cameraRepo;

	@Autowired
	private ScheduleRepo scheduleRepo;

	@Autowired
	private IImageRepo imageRepo;

	@Autowired
	private IVideoRepo videoRepo;

	@Override
	public void capture(Long cameraId) throws IOException, InterruptedException {
		LOGGER.info(">>>>>> START CAPTURE >>>>>>");
		DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
		String fileName = df.format(new Date());
		Camera camera = cameraRepo.findByCameraid(cameraId);
		String cmd = "vlc " + camera.getCameraUrl()
				+ "  --rate=1 --video-filter=scene --vout=dummy --start-time=0 --stop-time=1 --scene-format=jpeg --scene-prefix="
				+ fileName
				+ " --scene-replace --scene-path=C:\\Users\\BinhHoc\\Documents\\GitHub\\CAMERA-SERVER\\src\\main\\webapp\\resources\\images"
				+ " vlc://quit";
		LOGGER.info("cmd ==== {}", cmd);
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(cmd);
		process.waitFor();
		String result = "http://localhost:8080/images/" + fileName + ".jpeg";
		Image image = new Image();
		image.setCamera(camera);
		image.setDate(new Date());
		image.setImageUrl(result);
		imageRepo.save(image);
		LOGGER.info(">>>>>> END CAPTURE >>>>>>");
	}

	@Override
	public void record(Long cameraId) throws IOException, InterruptedException {
		LOGGER.info(">>>>>> START RECORD >>>>>>");
		DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
		String fileName = df.format(new Date()) + ".ogg";
		Camera camera = cameraRepo.findByCameraid(cameraId);

		CamearSchedule schedule = scheduleRepo.findBycamera(camera);
		String cmd = "vlc " + camera.getCameraUrl()
				+ "  --sout=#transcode{vcodec=theo}:std{access=file,mux=ogg,dst=C:\\Users\\BinhHoc\\Documents\\GitHub\\CAMERA-SERVER\\src\\main\\webapp\\resources\\videos\\"
				+ fileName + " --stop-time=" + schedule.getRecordTime() + " vlc://quit";
		LOGGER.info("cmd ==== {}", cmd);
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(cmd);
		process.waitFor();
		String result = "http://localhost:8080/videos/" + fileName;
		Video video = new Video();
		video.setCamera(camera);
		video.setDate(new Date());
		video.setVideoUrl(result);
		videoRepo.save(video);

		LOGGER.info(">>>>>> END RECORD >>>>>>");

	}

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
		DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
		String videoName = df.format(new Date()) + ".mp4";
		String fileName = "C:/Users/BinhHoc/Documents/GitHub/CAMERA-SERVER/src/main/webapp/resources/videos/"
				+ videoName;

		Camera camera = cameraRepo.findByCameraid(cameraId);

		CamearSchedule schedule = scheduleRepo.findBycamera(camera);

		double FRAME_RATE = 50;

		int SECONDS_TO_RUN_FOR = schedule.getRecordTime();

		// let's make a IMediaWriter to write the file.
		IMediaWriter writer = ToolFactory.makeWriter(fileName);

		Dimension screenBounds = Toolkit.getDefaultToolkit().getScreenSize();

		// We tell it we're going to add one video stream, with id 0,
		// at position 0, and that it will have a fixed frame rate of
		// FRAME_RATE.
		writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, screenBounds.width / 2, screenBounds.height / 2);

		long startTime = System.nanoTime();

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		VideoCapture videoCapture = new VideoCapture(camera.getCameraUrl());
		Mat frame = new Mat();

		for (int index = 0; index < SECONDS_TO_RUN_FOR * FRAME_RATE; index++) {

			if (videoCapture.read(frame)) {
				// take the screen shot
				BufferedImage screen = recordMatToBufferedImage(frame);
				// convert to the right image type
				BufferedImage bgrScreen = convertToType(screen, 5);

				// encode the image to stream #0
				writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);

				// sleep for frame rate milliseconds
				try {
					Thread.sleep((long) (1000 / FRAME_RATE));
				} catch (InterruptedException e) {
					// ignore
				}

			}

		}
		LOGGER.info(">>>>>> END RECORD >>>>>>");
		writer.close();

		String result = "http://localhost:8080/videos/" + videoName;
		Video video = new Video();
		video.setCamera(camera);
		video.setDate(new Date());
		video.setVideoUrl(result);
		videoRepo.save(video);
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
