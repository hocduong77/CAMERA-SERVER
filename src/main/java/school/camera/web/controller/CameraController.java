package school.camera.web.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.DateFormat;
import java.text.ParseException;
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
import javax.validation.Valid;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.dao.IImageRepo;
import school.camera.persistence.dao.UserRepository;
import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Image;
import school.camera.persistence.model.User;
import school.camera.persistence.service.CameraDto;
import school.camera.persistence.service.Message;
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
	private QuartzConfiguration quart;
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	private static boolean isStream = true;

	private static HashMap<Long, Process> streamList = new HashMap<Long, Process>();

	public CameraController() {

	}

	@RequestMapping(value = "/homepage", method = RequestMethod.GET)
	public ModelAndView homepage(HttpServletRequest request, Model model) throws IOException {
		isStream = false;
		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);
		List<Camera> cameras = cameraRepo.findByUser(user);
		List<CameraDto> cameraDtos = new ArrayList<CameraDto>();
		for (Camera camera : cameras) {
			CameraDto cameraDto = new CameraDto();
			cameraDto.setStreamUrl(startStream(camera));
			cameraDto.setCameraId(camera.getCameraid());
			cameraDtos.add(cameraDto);
		}
		if (isStream == true) {
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	private Map<String, String> getHour() {
		Map<String, String> hour = new LinkedHashMap<String, String>();
		hour.put("00:00", "00:00");
		hour.put("01:00", "01:00");
		hour.put("02:00", "02:00");
		hour.put("03:00", "03:00");
		hour.put("04:00", "04:00");
		hour.put("05:00", "05:00");
		hour.put("06:00", "06:00");
		hour.put("07:00", "07:00");
		hour.put("08:00", "08:00");
		hour.put("09:00", "09:00");
		hour.put("10:00", "10:00");
		hour.put("11:00", "11:00");
		hour.put("12:00", "12:00");
		hour.put("13:00", "13:00");
		hour.put("14:00", "14:00");
		hour.put("15:00", "15:00");
		hour.put("16:00", "16:00");
		hour.put("17:00", "17:00");
		hour.put("18:00", "18:00");
		hour.put("19:00", "19:00");
		hour.put("20:00", "20:00");
		hour.put("21:00", "21:00");
		hour.put("22:00", "22:00");
		hour.put("23:00", "23:00");
		return hour;
	}

	@RequestMapping(value = "/setting/{id}", method = RequestMethod.GET)
	public ModelAndView settingCamera(HttpServletRequest request, Model model, @PathVariable("id") Long cameraId) {
		Camera camera = cameraRepo.findByCameraid(cameraId);
		LOGGER.info("setting page {}", cameraId);
		CameraDto cameraDto = convert(camera);

		Map<Integer, String> time = getTimeInterval();
		Map<String, String> hour = getHour();
		ModelAndView mav = new ModelAndView("setting", "camera", cameraDto);
		mav.addObject("time", time);
		mav.addObject("hour", hour);
		return mav;

	}

	@RequestMapping(value = "/setting/{id}", method = RequestMethod.POST)
	public ModelAndView updateCamera(HttpServletRequest request, Model model,
			@ModelAttribute("camera") CameraDto cameraDto, @PathVariable("id") Long cameraId)
			throws ParseException, SchedulerException {
		Camera camera = cameraRepo.findByCameraid(cameraId);
		if (camera == null) {
			return new ModelAndView("setting", "cameras", cameraDto);
		}
		// LOGGER.info("setting alias {}", cameraDto.getAlias());
		// camera.setAlias(cameraDto.getAlias());
		camera.setCameraUrl(cameraDto.getCameraUrl());
		camera.setCapture(cameraDto.isCapture());
		camera.setCaptureTime(cameraDto.getCaptureTime());
		camera.setEnabled(cameraDto.isEnabled());
		camera.setName(cameraDto.getName());
		camera.setRecord(cameraDto.isRecord());
		camera.setRecordTime(cameraDto.getRecordTime());
		DateFormat sdf = new SimpleDateFormat("hh:mm");
		Date date = sdf.parse(cameraDto.getRecordSchedule());
		camera.setRecordSchedule(date);
		//camera.setRecordSchedule(new Date());
		cameraRepo.save(camera);

		if (camera.isCapture() == true && camera.getCaptureTime() > 0) {
			quart.createCaptureTrigger(camera);
		} else {
			quart.deleteJob(camera.getCameraid(), "capture");
		}

		if (camera.isRecord() == true && camera.getRecordTime() > 0 && camera.getRecordSchedule() != null) {
			quart.createRecordTrigger(camera);
		} else {
			quart.deleteJob(camera.getCameraid(), "record");
		}

		Map<Integer, String> time = getTimeInterval();
		Map<String, String> hour = getHour();
		ModelAndView mav = new ModelAndView("setting", "camera", cameraDto);
		mav.addObject("time", time);
		mav.addObject("hour", hour);

		return mav;
	}

	@RequestMapping(value = "/cameras", method = RequestMethod.GET)
	public ModelAndView showCamera(HttpServletRequest request, Model model) {
		LOGGER.info("Rendering camera page.");
		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		List<CameraDto> cameras = getListCameras();
		return new ModelAndView("cameras", "cameras", cameras);
	}

	@RequestMapping(value = "/addcamera", method = RequestMethod.GET)
	public String addCamera(WebRequest request, Model model) {
		LOGGER.info("Rendering registration page.");
		return "addcamera";
	}


	@RequestMapping(value = "/capture", method = RequestMethod.POST)
	public @ResponseBody String capture(HttpServletRequest request, Model model) throws IOException, InterruptedException {
		Long cameraId = Long.parseLong(request.getParameter("cameraId"));
		LOGGER.info("Rendering test api.{}", cameraId);
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
		return result;
	}
	
	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public @ResponseBody String testCamera(HttpServletRequest request, Model model) throws IOException {
		String url = request.getParameter("url");
		LOGGER.info("Rendering test api.{}", url);
		HttpSession session = request.getSession(false);
		session.setAttribute("camera_test_url", url);
		int port = getFreePort();
		String cmd = "vlc.exe -I dummy " + url
				+ " :network-caching=1000 :sout=#transcode{vcodec=theo,vb=1600,scale=1,acodec=none}:http{mux=ogg,dst=:"
				+ Integer.toString(port) + "/stream} :no-sout-rtp-sap :no-sout-standard-sap :sout-keep vlc://quit";
		LOGGER.info("cmd ==== {}", cmd);
		
		Runtime runtime = Runtime.getRuntime();

		runtime.exec(cmd);
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.info("return stream url");
		String result = "<video id=\"video\" src=\"http://localhost:" + Integer.toString(port)
				+ "/stream\" type=\"video/ogg; codecs=theora\" autoplay=\"autoplay\"/>";
		LOGGER.info("result ==== {}", result);
		return result;
	}

	@RequestMapping(value = "/saveCamera", method = RequestMethod.GET)
	public ModelAndView saveCamera(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession(false);
		String cameraUrl = (String) session.getAttribute("camera_test_url");
		LOGGER.info("camera_test_url {}", cameraUrl);
		String alias = generateAlias();
		Camera camera = new Camera();
		camera.setAlias(alias);
		camera.setName(alias);
		camera.setCameraUrl(cameraUrl);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);
		camera.setUser(user);
		cameraRepo.save(camera);
		List<CameraDto> cameras = getListCameras();

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

	private List<CameraDto> getListCameras() {
		List<Camera> cameras = cameraRepo.findAll();
		List<CameraDto> cameraDtos = new ArrayList<CameraDto>();

		for (Camera camera : cameras) {
			CameraDto cameraDto = new CameraDto();
			cameraDto.setCameraId(camera.getCameraid());
			cameraDto.setAlias(camera.getAlias());
			cameraDto.setName(camera.getName());
			cameraDto.setCameraUrl(camera.getCameraUrl());
			cameraDtos.add(cameraDto);
		}
		return cameraDtos;
	}

	private int getFreePort() throws IOException {
		ServerSocket socket = new ServerSocket(0);
		int port = socket.getLocalPort();
		socket.close();
		return port;
	}

	private String startStream(Camera camera) throws IOException {
		Process process = streamList.get(camera.getCameraid());
		if (null != process) {
			return camera.getStreamUrl();
		}
		int port = getFreePort();
		String cmd = "vlc.exe -I dummy " + camera.getCameraUrl()
				+ " :network-caching=1000 :sout=#transcode{vcodec=theo,vb=1600,scale=1,acodec=none}:http{mux=ogg,dst=:"
				+ Integer.toString(port) + "/stream} :no-sout-rtp-sap :no-sout-standard-sap :sout-keep vlc://quit";
		LOGGER.info("cmd ==== {}", cmd);
		Runtime runtime = Runtime.getRuntime();
		streamList.put(camera.getCameraid(), runtime.exec(cmd));
		LOGGER.info("port auto gnerate {}", port);
		String streamUrl = "http://localhost:" + Integer.toString(port) + "/stream";
		camera.setStreamUrl(streamUrl);
		cameraRepo.save(camera);
		isStream = true;
		return streamUrl;
	}

	private CameraDto convert(Camera camera) {
		CameraDto cameraDto = new CameraDto();
		cameraDto.setAlias(camera.getAlias());
		cameraDto.setCameraId(camera.getCameraid());
		cameraDto.setCameraUrl(camera.getCameraUrl());
		cameraDto.setCapture(camera.isCapture());
		cameraDto.setCaptureTime(camera.getCaptureTime());
		cameraDto.setEnabled(camera.isEnabled());
		cameraDto.setName(camera.getName());
		cameraDto.setRecord(camera.isRecord());
		if (camera.getRecordSchedule() != null) {
			cameraDto.setRecordSchedule(camera.getRecordSchedule().toString());
		}
		cameraDto.setRecordTime(camera.getRecordTime());
		return cameraDto;
	}

}
