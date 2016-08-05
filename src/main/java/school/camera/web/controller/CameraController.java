package school.camera.web.controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.dao.UserRepository;
import school.camera.persistence.model.Camera;
import school.camera.persistence.model.User;
import school.camera.persistence.service.CameraDto;
import school.camera.persistence.service.Message;

@Controller
public class CameraController {

    @Autowired
    private CameraRepo cameraRepo;
    

    @Autowired
    private UserRepository userRepo;
    
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	private static boolean isStream = true;

	private static HashMap<Long, Process> streamList = new HashMap<Long, Process>();
	public CameraController() {

	}
	
	@RequestMapping(value = "/homepage", method = RequestMethod.GET)
    public ModelAndView homepage(HttpServletRequest  request, Model model) throws IOException {
		isStream = false;
		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);
		List<Camera> cameras =  cameraRepo.findByUser(user);
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
		
		return new ModelAndView("homepage","cameras", cameraDtos);
    } 
	
	
	@RequestMapping(value = "/setting/{id}", method = RequestMethod.GET)
	public ModelAndView settingCamera(HttpServletRequest  request, Model model, @PathVariable("id") Long cameraId) {
		Camera camera = cameraRepo.findByCameraid(cameraId);
		LOGGER.info("setting page {}", cameraId);
		CameraDto cameraDto = new CameraDto();
		cameraDto.setCameraId(camera.getCameraid());
		cameraDto.setAlias(camera.getAlias());
		cameraDto.setCameraUrl(camera.getCameraUrl());
		return new ModelAndView("setting","camera", camera);
	}


	@RequestMapping(value = "/cameras", method = RequestMethod.GET)
	public ModelAndView showCamera(HttpServletRequest  request, Model model) {
		LOGGER.info("Rendering camera page.");
		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		List<CameraDto> cameras = getListCameras();
		return new ModelAndView("cameras","cameras", cameras);
	}

	@RequestMapping(value = "/addcamera", method = RequestMethod.GET)
	public String addCamera(WebRequest request, Model model) {
		LOGGER.info("Rendering registration page.");
		return "addcamera";
	}

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public @ResponseBody String testCamera(HttpServletRequest  request, Model model) throws IOException {
		String url = request.getParameter("url");
		LOGGER.info("Rendering test api.{}", url);
		HttpSession session = request.getSession(false);
		session.setAttribute("camera_test_url", url);			
		int port = getFreePort();		
		String cmd = "vlc.exe -I dummy "+ url +" :network-caching=1000 :sout=#transcode{vcodec=theo,vb=1600,scale=1,acodec=none}:http{mux=ogg,dst=:"+Integer.toString(port)+"/stream} :no-sout-rtp-sap :no-sout-standard-sap :sout-keep vlc://quit";
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
		String result = "<video id=\"video\" src=\"http://localhost:"+Integer.toString(port)+"/stream\" type=\"video/ogg; codecs=theora\" autoplay=\"autoplay\"/>";
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
		
		return new ModelAndView("cameras","cameras", cameras);
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
		List<Camera> cameras =  cameraRepo.findAll();
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
	
	private int getFreePort() throws IOException{
		ServerSocket socket = new ServerSocket(0);
		int port = socket.getLocalPort();
		socket.close();
		return port;
	}
	
	private String startStream(Camera camera) throws IOException{	
		Process process= streamList.get(camera.getCameraid());
		if (null != process) {
			return camera.getStreamUrl();
		}
		int port = getFreePort();
		String cmd = "vlc.exe -I dummy "+ camera.getCameraUrl() +" :network-caching=1000 :sout=#transcode{vcodec=theo,vb=1600,scale=1,acodec=none}:http{mux=ogg,dst=:"+Integer.toString(port)+"/stream} :no-sout-rtp-sap :no-sout-standard-sap :sout-keep vlc://quit";
		LOGGER.info("cmd ==== {}", cmd);
		Runtime runtime = Runtime.getRuntime();	
		streamList.put(camera.getCameraid(), runtime.exec(cmd));
		LOGGER.info("port auto gnerate {}", port);
		String streamUrl =   "http://localhost:"+Integer.toString(port)+"/stream";
		camera.setStreamUrl(streamUrl);
		cameraRepo.save(camera);
		isStream = true;
		return streamUrl;
	}


}
