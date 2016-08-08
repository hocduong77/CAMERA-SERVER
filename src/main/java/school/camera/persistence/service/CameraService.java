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
import school.camera.persistence.dao.UserRepository;
import school.camera.persistence.model.Camera;

@Service
@Transactional
public class CameraService implements ICameraService {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private CameraRepo cameraRepo;

	@Override
	public void capture(Long cameraId) throws IOException {
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
		runtime.exec(cmd);

		LOGGER.info(">>>>>> END CAPTURE >>>>>>");
	}

	@Override
	public void record(Long cameraId) throws IOException {
		LOGGER.info(">>>>>> START CAPTURE >>>>>>");
		DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
		String fileName = df.format(new Date()) + ".ogg";
		Camera camera = cameraRepo.findByCameraid(cameraId);
		String cmd = "vlc " + camera.getCameraUrl()
				+ "  --sout=#transcode{vcodec=theo}:std{access=file,mux=ogg,dst=C:\\Users\\BinhHoc\\Documents\\GitHub\\CAMERA-SERVER\\src\\main\\webapp\\resources\\videos\\"
				+ fileName + " --stop-time=" + camera.getRecordTime() + " vlc://quit";
		LOGGER.info("cmd ==== {}", cmd);
		Runtime runtime = Runtime.getRuntime();
		runtime.exec(cmd);

		LOGGER.info(">>>>>> END CAPTURE >>>>>>");

	}

}
