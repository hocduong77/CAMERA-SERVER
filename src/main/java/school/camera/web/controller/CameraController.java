package school.camera.web.controller;

import java.io.IOException;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.model.Camera;

@Controller
public class CameraController {

    @Autowired
    private CameraRepo cameraRepo;
    
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	public CameraController() {

	}

	@RequestMapping(value = "/cameras", method = RequestMethod.GET)
	public String showCamera(WebRequest request, Model model) {
		LOGGER.info("Rendering camera page.");
		return "cameras";
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
		String cmd = "vlc.exe "+ url +" :network-caching=1000 :sout=#transcode{vcodec=theo,vb=1600,scale=1,acodec=none}:http{mux=ogg,dst=:8181/stream} :no-sout-rtp-sap :no-sout-standard-sap :sout-keep vlc://quit";
		LOGGER.info("cmd ==== {}", cmd);
		Runtime runtime = Runtime.getRuntime();
		runtime.exec(cmd);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.info("return stream url");
		String result = "<video id=\"video\" src=\"http://localhost:8181/stream\" type=\"video/ogg; codecs=theora\" autoplay=\"autoplay\"/>";
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
		cameraRepo.save(camera);
		return new ModelAndView("cameras");
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

}
