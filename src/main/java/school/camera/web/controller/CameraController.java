package school.camera.web.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.dao.IImageRepo;
import school.camera.persistence.dao.INotificationRepo;
import school.camera.persistence.dao.IVideoRepo;
import school.camera.persistence.dao.ScheduleRepo;
import school.camera.persistence.dao.UserRepository;
import school.camera.persistence.model.CamearSchedule;
import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Image;
import school.camera.persistence.model.User;
import school.camera.persistence.service.CameraDto;
import school.camera.persistence.service.ICameraService;
import school.camera.persistence.service.Server2;
import school.camera.persistence.service.UserDto;
import school.camera.spring.QuartzConfiguration;

@Controller
public class CameraController {

	@Autowired
	private CameraRepo cameraRepo;

	@Autowired
	private IImageRepo imageRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ScheduleRepo scheduleRepo;

	@Autowired
	private ICameraService cameraService;

	@Autowired
	private IVideoRepo videoRepo;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private INotificationRepo notificationRepo;
	@Autowired
	private QuartzConfiguration quart;
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private static boolean isStream = true;

	private static HashMap<Long, HashMap<Long, Server2>> streamList = new HashMap<Long, HashMap<Long, Server2>>();
	private static HashMap<Long, ServerSocket> listenerList = new HashMap<Long, ServerSocket>();

	public CameraController() {

	}

	@RequestMapping(value = "/startStop", method = RequestMethod.GET)
	public @ResponseBody String startStop(HttpServletRequest request, Model model) throws IOException {

		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);

		Long cameraId = Long.parseLong(request.getParameter("cameraId"));

		LOGGER.info("Rendering startStop cameraId {} with {} height {}", cameraId);

		HashMap<Long, Server2> userCamera = streamList.get(user.getUserid());
		Server2 process = userCamera.get(cameraId);

		if (process != null) {
			LOGGER.info("startStop state {}", process.startStop);
			process.startStop = !process.startStop;
		}
		String result = "success";
		return result;
	}

	@RequestMapping(value = "/sec_setting", method = RequestMethod.GET)
	public @ResponseBody String securitySetting(HttpServletRequest request, Model model) throws IOException {

		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);

		Long cameraId = Long.parseLong(request.getParameter("cameraId"));
		double with = Double.parseDouble(request.getParameter("with"));
		double height = Double.parseDouble(request.getParameter("height"));
		LOGGER.info("Rendering test cameraId {} with {} height {}", cameraId, with, height);

		HashMap<Long, Server2> userCamera = streamList.get(user.getUserid());
		Server2 process = userCamera.get(cameraId);

		if (process != null) {
			process.objectWith = with;
			process.objectHeight = height;
			Camera camera = cameraRepo.findByCameraid(cameraId);
			camera.setObjectWith(with);
			camera.setObjectHeight(height);
			cameraRepo.save(camera);
		}
		String result = cameraId + "/ " + with + "/" + height;
		return result;
	}

	@RequestMapping(value = "/homepage", method = RequestMethod.GET)
	public ModelAndView homepage(HttpServletRequest request, Model model) throws IOException {
		// isStream = false;
		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);
		// List<Camera> cameras = cameraRepo.findByUser(user);
		List<Camera> cameras = new ArrayList<Camera>();
		if (user.getRole().getRole() == 3) {
			cameras = cameraRepo.findBySecurityId(user.getUserid());
		} else {
			cameras = cameraRepo.findByUser(user);
		}

		List<CameraDto> cameraDtos = new ArrayList<CameraDto>();
		for (Camera camera : cameras) {
			if (camera.isEnabled()) {
				CameraDto cameraDto = new CameraDto();
				cameraDto.setPort(startStream(camera, user.getUserid()));
				cameraDto.setCameraId(camera.getCameraid());
				cameraDto.setObjectWith(camera.getObjectWith());
				cameraDto.setObjectHeight(camera.getObjectHeight());
				cameraDtos.add(cameraDto);
			}

		}
		return new ModelAndView("homepage", "cameras", cameraDtos);
	}

	private Map<Integer, String> getTimeInterval() {
		Map<Integer, String> time = new LinkedHashMap<Integer, String>();
		time.put(60, "1 min");
		time.put(300, "5 min");
		time.put(600, "10 min");
		time.put(900, "15 min");
		time.put(1800, "30 min");
		time.put(3600, "1 hour");
		time.put(7200, "2 hour");
		time.put(14400, "4 hours");
		time.put(28800, "8 hours");
		time.put(43200, "12 hours");
		time.put(86400, "24 hours");
		return time;
	}

	@RequestMapping(value = "/setting/{id}", method = RequestMethod.GET)
	public ModelAndView settingCamera(HttpServletRequest request, Model model, @PathVariable("id") Long cameraId) {
		Camera camera = cameraRepo.findByCameraid(cameraId);
		LOGGER.info("setting page {}", cameraId);
		CameraDto cameraDto = convert(camera);

		Map<Integer, String> time = getTimeInterval();
		Map<Long, String> securitiy = new HashMap<Long, String>();
		ModelAndView mav = new ModelAndView("setting", "camera", cameraDto);
		mav.addObject("time", time);
		List<User> users = userRepo.findAll();
		List<UserDto> userDtos = new ArrayList<UserDto>();
		for (User user : users) {
			if (user.getRole().getRole() == 3) {
				UserDto userDto = new UserDto();
				userDto.setEmail(user.getEmail());
				userDto.setFirstName(user.getFirstName());
				userDto.setLastName(user.getLastName());
				userDtos.add(userDto);
				securitiy.put(user.getUserid(), user.getFirstName() + " " + user.getLastName());
			}
		}

		mav.addObject("users", securitiy);
		return mav;

	}

	@RequestMapping(value = "/setting/{id}", method = RequestMethod.POST)
	public ModelAndView updateCamera(HttpServletRequest request, Model model,
			@ModelAttribute("camera") CameraDto cameraDto, @PathVariable("id") Long cameraId)
			throws SchedulerException {
		Camera camera = cameraRepo.findByCameraid(cameraId);

		if (camera == null) {
			return new ModelAndView("setting", "cameras", cameraDto);
		}
		camera.setSecurityId(cameraDto.getSecurityId());
		camera.setSecurity(cameraDto.isSecurity());
		if (cameraDto.isEnabled() == false) {
			camera.setEnabled(false);
			// delete all schedule
			quart.deleteJob(camera.getCameraid(), "capture");
			quart.deleteJob(camera.getCameraid(), "record");
		} else {
			try {

				DateFormat timeFormat = new SimpleDateFormat("hh:mm");
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				CamearSchedule schedule = scheduleRepo.findBycamera(camera);
				if (schedule == null) {
					schedule = new CamearSchedule();
				}
				camera.setCameraUrl(cameraDto.getCameraUrl());
				camera.setEnabled(cameraDto.isEnabled());
				camera.setName(cameraDto.getName());

				schedule.setCapture(cameraDto.isCapture());
				if (cameraDto.isCapture()) {
					schedule.setCaptureTime(cameraDto.getCaptureTime());

					schedule.setCaptureRepeat(cameraDto.isCaptureRepeat());
					if (cameraDto.isCaptureRepeat() == false) {
						schedule.setCaptureFrom(dateFormat.parse(cameraDto.getCaptureFrom()));
						schedule.setCaptureTo(dateFormat.parse(cameraDto.getCaptureTo()));
					}
				}

				schedule.setRecord(cameraDto.isRecord());

				if (cameraDto.isRecord()) {
					schedule.setRecordTime(timeFormat.parse(cameraDto.getRecordTime()));

					schedule.setRecordSchedule(timeFormat.parse(cameraDto.getRecordSchedule()));

					schedule.setRecordRepeat(cameraDto.isRecordRepeat());
					LOGGER.info("isRecordRepeat {}", cameraDto.isRecordRepeat());
					if (cameraDto.isRecordRepeat() == false) {
						schedule.setRecordFrom(dateFormat.parse(cameraDto.getRecordFrom()));
						schedule.setRecordTo(dateFormat.parse(cameraDto.getRecordTo()));
					}
				}

				schedule.setCamera(camera);

				camera.setSchedule(schedule);
				// camera.setRecordSchedule(new Date());
				cameraRepo.save(camera);

				if (schedule.isCapture() == true && schedule.getCaptureTime() > 0) {
					quart.createCaptureTrigger(camera, schedule);
				} else {
					quart.deleteJob(camera.getCameraid(), "capture");
				}

				if (schedule.isRecord() == true && schedule.getRecordTime() != null
						&& schedule.getRecordSchedule() != null) {
					quart.createRecordTrigger(camera, schedule);
				} else {
					quart.deleteJob(camera.getCameraid(), "record");
				}
			} catch (Exception e) {
				e.printStackTrace();
				Map<Integer, String> time = getTimeInterval();
				String error = "Invalid Input";
				ModelAndView mav = new ModelAndView("setting", "camera", cameraDto);
				mav.addObject("time", time);
				mav.addObject("mess", error);
				return mav;
			}

		}

		Map<Integer, String> time = getTimeInterval();
		String success = "save successful";
		ModelAndView mav = new ModelAndView("setting", "camera", cameraDto);
		mav.addObject("time", time);
		mav.addObject("mess", success);
		Map<Long, String> securitiy = new HashMap<Long, String>();
		List<User> users = userRepo.findAll();
		List<UserDto> userDtos = new ArrayList<UserDto>();
		for (User user : users) {
			if (user.getRole().getRole() == 3) {
				UserDto userDto = new UserDto();
				userDto.setEmail(user.getEmail());
				userDto.setFirstName(user.getFirstName());
				userDto.setLastName(user.getLastName());
				userDtos.add(userDto);
				securitiy.put(user.getUserid(), user.getFirstName() + " " + user.getLastName());
			}
		}

		mav.addObject("users", securitiy);
		return mav;
	}

	@RequestMapping(value = "/cameras", method = RequestMethod.GET)
	public ModelAndView showCamera(HttpServletRequest request, Model model) {
		LOGGER.info("Rendering camera page.");
		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		User user = userRepo.findByEmail(email);

		LOGGER.info("username {}", email);
		List<CameraDto> cameras = getListCameras(user);
		return new ModelAndView("cameras", "cameras", cameras);
	}

	@RequestMapping(value = "/addcamera", method = RequestMethod.GET)
	public String addCamera(WebRequest request, Model model) {
		LOGGER.info("Rendering registration page.");
		return "addcamera";
	}

	@RequestMapping(value = "/capture", method = RequestMethod.POST)
	public @ResponseBody String capture(HttpServletRequest request, Model model)
			throws IOException, InterruptedException {
		Long cameraId = Long.parseLong(request.getParameter("cameraId"));
		LOGGER.info("Rendering test api.{}", cameraId);
		// DateFormat df = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
		// String fileName = df.format(new Date());
		Camera camera = cameraRepo.findByCameraid(cameraId);
		// String cmd = "vlc " + camera.getCameraUrl()
		// + " --rate=1 --video-filter=scene --vout=dummy --start-time=0
		// --stop-time=1 --scene-format=jpeg --scene-prefix="
		// + fileName
		// + " --scene-replace
		// --scene-path=C:\\Users\\BinhHoc\\Documents\\GitHub\\CAMERA-SERVER\\src\\main\\webapp\\resources\\images"
		// + " vlc://quit";
		// LOGGER.info("cmd ==== {}", cmd);
		// Runtime runtime = Runtime.getRuntime();
		// Process process = runtime.exec(cmd);
		// process.waitFor();
		// String result = "http://localhost:8080/images/" + fileName + ".jpeg";
		String result = cameraService.captureNew(cameraId);
		Image image = new Image();
		image.setCamera(camera);
		image.setDate(new Date());
		image.setImageUrl(result);
		imageRepo.save(image);
		return result;
	}

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public @ResponseBody String testCamera(HttpServletRequest request, Model model) throws IOException {
		String url = request.getParameter("url");
		LOGGER.info("Rendering test api.{}", url);
		HttpSession session = request.getSession(false);
		session.setAttribute("camera_test_url", url);

		ServerSocket listener = new ServerSocket(4499);

		Server2 streamingServer = new Server2();
		streamingServer.setUrl(url);
		streamingServer.setRTP_dest_port(4499);
		streamingServer.listener = listener;
		streamingServer.start();

		// session.setAttribute("test_camera", process);
		LOGGER.info("return stream url");
		String resultTest = "<applet name=\"Test\""
				+ "CODEBASE=\"http://localhost:8080/camera-server/resources/resourceapplet\""
				+ "code=\"camera.class\" width=\"480\" height=\"280\">" + "<param name=\"rtpPort\" value=\"4499\" />"
				+ "<param name=\"width\" value=\"480\" />" + "<param name=\"height\" value=\"280\" />"
				+ "<param name=\"separate_jvm\" value=\"true\">" + "</applet>";
		//
		// String resultTest = "<video id=\"video\" src=\"http://localhost:" +
		// Integer.toString(port)
		// + "/stream\" type=\"video/ogg; codecs=theora\"
		// autoplay=\"autoplay\"/>";
		LOGGER.info("result ==== {}", resultTest);
		return resultTest;
	}

	@RequestMapping(value = "/saveCamera", method = RequestMethod.GET)
	public ModelAndView saveCamera(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession(false);
		String cameraUrl = (String) session.getAttribute("camera_test_url");
		// Process process = (Process) session.getAttribute("test_camera");
		LOGGER.info("camera_test_url {}", cameraUrl);
		String alias = generateAlias();
		Camera camera = new Camera();
		camera.setEnabled(true);
		camera.setAlias(alias);
		camera.setName(alias);
		camera.setCameraUrl(cameraUrl);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);
		camera.setUser(user);
		cameraRepo.save(camera);
		List<CameraDto> cameras = getListCameras(user);
		// process.destroy();
		return new ModelAndView("cameras", "cameras", cameras);
	}

	private String generateAlias() {
		char[] chars = "1234567890abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 13; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		String result = sb.toString();
		return result;
	}

	private List<CameraDto> getListCameras(User user) {
		List<Camera> cameras = cameraRepo.findByUser(user);
		List<CameraDto> cameraDtos = new ArrayList<CameraDto>();

		for (Camera camera : cameras) {
			if (camera.isEnabled()) {
				CameraDto cameraDto = new CameraDto();
				cameraDto.setCameraId(camera.getCameraid());
				cameraDto.setAlias(camera.getAlias());
				cameraDto.setName(camera.getName());
				cameraDto.setCameraUrl(camera.getCameraUrl());
				cameraDtos.add(cameraDto);
			}

		}
		return cameraDtos;
	}

	private int getFreePort() throws IOException {
		ServerSocket socket = new ServerSocket(0);
		int port = socket.getLocalPort();
		socket.close();
		return port;
	}

	private int startStream(Camera camera, Long userId) throws IOException {
		ServerSocket listener = listenerList.get(camera.getCameraid());
		if (null == listener) {
			listener = new ServerSocket(camera.getPort().getPort());
			listenerList.put(camera.getCameraid(), listener);
		}

		Server2 streamingServer = new Server2();
		streamingServer.setUrl(camera.getCameraUrl());
		LOGGER.info("port {}", camera.getPort().getPort());
		streamingServer.setRTP_dest_port(camera.getPort().getPort());
		streamingServer.listener = listener;
		streamingServer.cameraId = camera.getCameraid();
		streamingServer.objectWith = camera.getObjectWith();
		streamingServer.objectHeight = camera.getObjectHeight();
		streamingServer.cameraRepo = cameraRepo;
		streamingServer.videoRepo = videoRepo;
		streamingServer.imageRepo = imageRepo;
		streamingServer.notificationRepo = notificationRepo;
		streamingServer.mailSender = mailSender;
		streamingServer.userRepo = userRepo;
		streamingServer.startStop = true;
		streamingServer.start();
		HashMap<Long, Server2> userCamera = streamList.get(userId);
		if (null == userCamera) {
			userCamera = new HashMap<Long, Server2>();
		}

		userCamera.put(camera.getCameraid(), streamingServer);
		streamList.put(userId, userCamera);
		return camera.getPort().getPort();
	}

	private CameraDto convert(Camera camera) {
		CamearSchedule schedule = scheduleRepo.findBycamera(camera);
		CameraDto cameraDto = new CameraDto();
		DateFormat timeFormat = new SimpleDateFormat("hh:mm");
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		if (null != schedule) {
			if (schedule.getRecordSchedule() != null) {
				cameraDto.setRecordSchedule(timeFormat.format(schedule.getRecordSchedule()));
			}
			if (schedule.getRecordTime() != null) {
				cameraDto.setRecordTime(timeFormat.format(schedule.getRecordTime()));
			}
			if (schedule.getCaptureFrom() != null) {
				cameraDto.setCaptureFrom(dateFormat.format(schedule.getCaptureFrom()));
			}

			if (schedule.getCaptureTo() != null) {
				cameraDto.setCaptureTo(dateFormat.format(schedule.getCaptureTo()));
			}

			if (schedule.getRecordFrom() != null) {
				cameraDto.setRecordFrom(dateFormat.format(schedule.getRecordFrom()));
			}
			if (schedule.getRecordTo() != null) {
				cameraDto.setRecordTo(dateFormat.format(schedule.getRecordTo()));
			}
			// schedule.setRecordTime(timeFormat.parse(cameraDto.getRecordTime()));
			cameraDto.setCapture(schedule.isCapture());
			cameraDto.setCaptureTime(schedule.getCaptureTime());
			// cameraDto.setRecordTime(schedule.getRecordTime());
			cameraDto.setRecord(schedule.isRecord());
			cameraDto.setCaptureRepeat(schedule.isCaptureRepeat());
			cameraDto.setRecordRepeat(schedule.isRecordRepeat());
		}

		cameraDto.setAlias(camera.getAlias());
		cameraDto.setCameraId(camera.getCameraid());
		cameraDto.setCameraUrl(camera.getCameraUrl());

		cameraDto.setEnabled(camera.isEnabled());
		cameraDto.setName(camera.getName());

		return cameraDto;
	}

}
