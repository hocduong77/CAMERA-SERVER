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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.dao.IImageRepo;
import school.camera.persistence.dao.INotificationRepo;
import school.camera.persistence.dao.IVideoRepo;
import school.camera.persistence.dao.UserRepository;
import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Image;
import school.camera.persistence.model.Notification;
import school.camera.persistence.model.User;
import school.camera.persistence.model.Video;
import school.camera.persistence.service.CameraDto;
import school.camera.persistence.service.NotificationDto;
import school.camera.persistence.service.OpenCv;
import school.camera.persistence.service.SearchDto;
import school.camera.persistence.service.Server2;
import school.camera.spring.QuartzConfiguration;

@Controller
public class MediaController {

	@Autowired
	private CameraRepo cameraRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private IVideoRepo videoRepo;

	@Autowired
	private IImageRepo imageRepo;

	@Autowired
	private INotificationRepo notificationRepo;

	@Autowired
	private QuartzConfiguration quart;
	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	public MediaController() {

	}

	@RequestMapping(value = "/opencv", method = RequestMethod.GET)
	public ModelAndView opencv(HttpServletRequest request, Model model) throws Exception {
		System.out.println(" ======================== " + System.getProperty("java.library.path"));
		// OpenCv opencv = new OpenCv("opencv");
		// opencv.start();
		Server2 server1 = new Server2();
		server1.setUrl("rtsp://192.168.1.100:554/live.sdp");
		server1.setRTP_dest_port(6699);
		server1.start();

		Server2 server2 = new Server2();
		server2.setUrl("rtsp://192.168.1.100:554/live.sdp");
		server2.setRTP_dest_port(8207);
		server2.start();
		ModelAndView mav = new ModelAndView("opencv");
		return mav;
	}

	@RequestMapping(value = "/notification/{id}", method = RequestMethod.GET)
	public ModelAndView getnotificationDetail(HttpServletRequest request, Model model,
			@PathVariable("id") Integer notificationId) throws IOException {
		Notification motification = notificationRepo.findOne(notificationId);
		Camera camera = cameraRepo.findOne(motification.getCameraId());
		List<Video> videos = videoRepo.findByNotificationId(notificationId);
		List<String> imageUrls = new ArrayList<String>();
		List<Image> images = imageRepo.findByNotificationId(notificationId);
		for (Image image : images) {
			imageUrls.add(image.getImageUrl());
		}

		ModelAndView mav = new ModelAndView("notificationDetail");

		mav.addObject("video", videos.get(0).getVideoUrl());
		mav.addObject("images", imageUrls);
		return mav;
	}

	@RequestMapping(value = "/notification", method = RequestMethod.GET)
	public ModelAndView getnotification(HttpServletRequest request, Model model) throws IOException {
		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);
		List<Camera> cameras = new ArrayList<Camera>();
		if (user.getRole().getRole() == 3) {
			cameras = cameraRepo.findBySecurityId(user.getUserid());
		} else {
			cameras = cameraRepo.findByUser(user);
		}
		List<NotificationDto> notificationDtos = new ArrayList<NotificationDto>();
		List<String> cameraAlias = new ArrayList<String>();
		for (Camera camera : cameras) {
			if (camera.isEnabled()) {
				cameraAlias.add(camera.getAlias());
				List<Notification> notifications = notificationRepo.findByCameraId(camera.getCameraid());
				for (Notification notification : notifications) {
					NotificationDto notificationDto = new NotificationDto();
					notificationDto.setCameraName(camera.getAlias());
					notificationDto.setId(notification.getId().toString());
					notificationDto.setTime(notification.getStartTime().toString());
					notificationDtos.add(notificationDto);
				}
			}

		}
		SearchDto search = new SearchDto();
		ModelAndView mav = new ModelAndView("notification", "search", search);
		mav.addObject("cameraAlias", cameraAlias);
		mav.addObject("notificationDtos", notificationDtos);
		return mav;
		// return new ModelAndView("image", "images", imageUrls );
	}

	@RequestMapping(value = "/notification", method = RequestMethod.POST)
	public ModelAndView searchnotification(HttpServletRequest request, Model model,
			@ModelAttribute("search") SearchDto searchDto) throws IOException {
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
		/* List<Camera> cameraSearch = cameraRepo.findByUser(user); */
		List<Camera> cameraSearch = new ArrayList<Camera>();
		if (user.getRole().getRole() == 3) {
			cameraSearch = cameraRepo.findBySecurityId(user.getUserid());
		} else {
			cameraSearch = cameraRepo.findByUser(user);
		}
		List<String> cameraAlias = new ArrayList<String>();
		for (Camera camera : cameraSearch) {
			cameraAlias.add(camera.getAlias());
		}
		List<NotificationDto> notificationDtos = new ArrayList<NotificationDto>();

		for (Camera camera : cameraSearch) {
			LOGGER.info("alias {}", searchDto.getAlias());
			if (camera.getAlias().equals(searchDto.getAlias())) {
				LOGGER.info("alias in db {}", camera.getAlias());
				List<Notification> notifications = notificationRepo.findByCameraId(camera.getCameraid());
				if (from == null || to == null) {
					notifications = notificationRepo.findByCameraId(camera.getCameraid());
				} else {
					notifications = notificationRepo.findByCameraIdAndStartTimeBetween(camera.getCameraid(), from, to);
				}

				for (Notification notification : notifications) {
					NotificationDto notificationDto = new NotificationDto();
					notificationDto.setCameraName(camera.getAlias());
					notificationDto.setId(notification.getId().toString());
					notificationDto.setTime(notification.getStartTime().toString());
					notificationDtos.add(notificationDto);
				}
			}

		}
		SearchDto search = new SearchDto();
		ModelAndView mav = new ModelAndView("notification", "search", search);
		mav.addObject("cameraAlias", cameraAlias);
		mav.addObject("notificationDtos", notificationDtos);
		return mav;

	}

	@RequestMapping(value = "/image", method = RequestMethod.GET)
	public ModelAndView images(HttpServletRequest request, Model model) throws IOException {

		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);
		List<Camera> cameras = new ArrayList<Camera>();
		if (user.getRole().getRole() == 3) {
			cameras = cameraRepo.findBySecurityId(user.getUserid());
		} else {
			cameras = cameraRepo.findByUser(user);
		}
		List<String> imageUrls = new ArrayList<String>();
		List<String> cameraAlias = new ArrayList<String>();
		for (Camera camera : cameras) {
			if (camera.isEnabled()) {
				cameraAlias.add(camera.getAlias());
				List<Image> images = imageRepo.findByCamera(camera);
				for (Image image : images) {
					imageUrls.add(image.getImageUrl());
				}
			}

		}
		SearchDto search = new SearchDto();
		ModelAndView mav = new ModelAndView("image", "search", search);
		mav.addObject("cameraAlias", cameraAlias);
		mav.addObject("images", imageUrls);
		return mav;
		// return new ModelAndView("image", "images", imageUrls );
	}

	@RequestMapping(value = "/image", method = RequestMethod.POST)
	public ModelAndView searchimages(HttpServletRequest request, Model model,
			@ModelAttribute("search") SearchDto searchDto) throws IOException {

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
		/* List<Camera> cameraSearch = cameraRepo.findByUser(user); */
		List<Camera> cameraSearch = new ArrayList<Camera>();
		if (user.getRole().getRole() == 3) {
			cameraSearch = cameraRepo.findBySecurityId(user.getUserid());
		} else {
			cameraSearch = cameraRepo.findByUser(user);
		}
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
			} else {
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
	}

	@RequestMapping(value = "/video", method = RequestMethod.GET)
	public ModelAndView videos(HttpServletRequest request, Model model) throws IOException {

		HttpSession session = request.getSession(false);
		String email = (String) session.getAttribute("email");
		LOGGER.info("username {}", email);
		User user = userRepo.findByEmail(email);
		List<Camera> cameras = new ArrayList<Camera>();
		if (user.getRole().getRole() == 3) {
			cameras = cameraRepo.findBySecurityId(user.getUserid());
		} else {
			cameras = cameraRepo.findByUser(user);
		}
		List<String> videoUrls = new ArrayList<String>();
		List<String> cameraAlias = new ArrayList<String>();
		for (Camera camera : cameras) {
			if (camera.isEnabled()) {
				LOGGER.info("camera alias {}", camera.getAlias());
				cameraAlias.add(camera.getAlias());
				List<Video> videos = videoRepo.findByCamera(camera);
				for (Video video : videos) {
					LOGGER.info("videoUrls {}", video.getVideoUrl());
					videoUrls.add(video.getVideoUrl());
				}
			}

		}
		SearchDto search = new SearchDto();
		ModelAndView mav = new ModelAndView("video", "search", search);
		mav.addObject("cameraAlias", cameraAlias);
		mav.addObject("videos", videoUrls);
		return mav;
	}

	@RequestMapping(value = "/video", method = RequestMethod.POST)
	public ModelAndView searchvideos(HttpServletRequest request, Model model,
			@ModelAttribute("search") SearchDto searchDto) throws IOException {

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
		/* List<Camera> cameraSearch = cameraRepo.findByUser(user); */
		List<Camera> cameraSearch = new ArrayList<Camera>();
		if (user.getRole().getRole() == 3) {
			cameraSearch = cameraRepo.findBySecurityId(user.getUserid());
		} else {
			cameraSearch = cameraRepo.findByUser(user);
		}

		List<String> cameraAlias = new ArrayList<String>();
		for (Camera camera : cameraSearch) {
			if (camera.isEnabled()) {
				cameraAlias.add(camera.getAlias());
			}

		}
		List<Camera> cameras = cameraRepo.findByUserAndAlias(user, searchDto.getAlias());
		List<String> videoUrls = new ArrayList<String>();

		for (Camera camera : cameras) {
			if (camera.isEnabled()) {
				List<Video> videos = new ArrayList<Video>();
				if (from == null || to == null) {
					videos = videoRepo.findByCamera(camera);
				} else {
					videos = videoRepo.findByCameraAndDateBetween(camera, from, to);
				}

				for (Video video : videos) {
					videoUrls.add(video.getVideoUrl());
				}
			}

		}
		SearchDto search = new SearchDto();
		ModelAndView mav = new ModelAndView("video", "search", search);
		mav.addObject("cameraAlias", cameraAlias);
		mav.addObject("videos", videoUrls);
		return mav;
	}

}
