package school.camera.web.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.dao.IImageRepo;
import school.camera.persistence.dao.UserRepository;
import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Image;
import school.camera.persistence.model.User;
import school.camera.persistence.service.CameraDto;
import school.camera.persistence.service.SearchDto;
import school.camera.spring.QuartzConfiguration;

@Controller
public class MediaController {

	@Autowired
	private CameraRepo cameraRepo;

	@Autowired
	private UserRepository userRepo;
	

	@Autowired
	private IImageRepo imageRepo;
	
	@Autowired
	private QuartzConfiguration quart;
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	public MediaController() {

	}
	
	@RequestMapping(value = "/image", method = RequestMethod.GET)
	public ModelAndView images(HttpServletRequest request, Model model) throws IOException {
		
		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);
		List<Camera> cameras = cameraRepo.findByUser(user);
		List<String> imageUrls = new ArrayList<String>();
		List<String> cameraAlias = new ArrayList<String>();
		for (Camera camera : cameras) {
			cameraAlias.add(camera.getAlias());
			List<Image> images = imageRepo.findByCamera(camera);
			for (Image image : images) {
				imageUrls.add(image.getImageUrl());
			}
		}
		SearchDto search = new SearchDto();
		ModelAndView mav = new ModelAndView("image", "search", search);
		mav.addObject("cameraAlias", cameraAlias);
		mav.addObject("images", imageUrls);
		return mav;
		//return new ModelAndView("image", "images", imageUrls );
	}

	@RequestMapping(value = "/image", method = RequestMethod.POST)
	public ModelAndView searchimages(HttpServletRequest request, Model model,  @ModelAttribute("search") SearchDto searchDto) throws IOException {	
		
		DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date from = null;
		Date to = null;
		try {
			 from = sdf.parse(searchDto.getFrom());
			 to = sdf.parse(searchDto.getTo());
		} catch (Exception e) {
			LOGGER.info("to or to null {}");
		}
		LOGGER.info("from {}", from);
		LOGGER.info("to {}", to);
		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);
		List<Camera> cameraSearch = cameraRepo.findByUser(user);
		List<String> cameraAlias = new ArrayList<String>();
		for (Camera camera : cameraSearch) {
			cameraAlias.add(camera.getAlias());
		}
		List<Camera> cameras = cameraRepo.findByUserAndAlias(user, searchDto.getAlias());
		List<String> imageUrls = new ArrayList<String>();
		
		for (Camera camera : cameras) {
			List<Image> images = new ArrayList<Image>();
			if (from == null || to == null) {
				 images = imageRepo.findByCamera(camera);
			}else {
				 images = imageRepo.findByCameraAndDateBetween(camera, from, to);
			}
			
			for (Image image : images) {
				imageUrls.add(image.getImageUrl());
			}
		}
		SearchDto search = new SearchDto();
		ModelAndView mav = new ModelAndView("image", "search", search);
		mav.addObject("cameraAlias", cameraAlias);
		mav.addObject("images", imageUrls);
		return mav;
		//return new ModelAndView("image", "images", imageUrls );
	}

	
	@RequestMapping(value = "/video", method = RequestMethod.GET)
	public ModelAndView videos(HttpServletRequest request, Model model) throws IOException {
		
		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);
		/*List<Camera> cameras = cameraRepo.findByUser(user);
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
		}*/

		return new ModelAndView("video");
	}
}