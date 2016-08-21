package school.camera.persistence.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.dao.IImageRepo;
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
		String result = "http://localhost:8080/videos/" + fileName ;	
		Video video = new Video();
		video.setCamera(camera);
		video.setDate(new Date());
		video.setVideoUrl(result);
		videoRepo.save(video);
		
		LOGGER.info(">>>>>> END RECORD >>>>>>");

	}

}
