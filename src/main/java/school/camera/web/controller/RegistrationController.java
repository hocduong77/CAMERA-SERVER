package school.camera.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.stylesheets.LinkStyle;

import com.mysql.jdbc.BlobFromLocator;

import school.camera.event.OnRegistrationCompleteEvent;
import school.camera.persistence.dao.CameraRepo;
import school.camera.persistence.dao.IGatewayRepo;
import school.camera.persistence.dao.UserRepository;
import school.camera.persistence.model.Camera;
import school.camera.persistence.model.Gateway;
import school.camera.persistence.model.User;
import school.camera.persistence.model.VerificationToken;
import school.camera.persistence.service.GatewayDto;
import school.camera.persistence.service.IUserService;
import school.camera.persistence.service.SearchDto;
import school.camera.persistence.service.UserDto;
import school.camera.validation.service.EmailExistsException;

@Controller
public class RegistrationController {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	@Autowired
	private IUserService service;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private IGatewayRepo gatewayRepo;

	@Autowired
	private CameraRepo cameraRepo;

	@Autowired
	private MessageSource messages;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	public RegistrationController() {

	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index() {
		return "index";
	}

	@RequestMapping(value = "/user/registration", method = RequestMethod.GET)
	public String showRegistrationForm(WebRequest request, Model model) {
		LOGGER.debug("Rendering registration page.");
		UserDto accountDto = new UserDto();
		model.addAttribute("user", accountDto);
		return "registration";
	}

	@RequestMapping(value = "/securities", method = RequestMethod.GET)
	public String securities(WebRequest request, Model model) {
		LOGGER.debug("Rendering securities page.");
		List<User> users = userRepo.findAll();
		List<UserDto> userDtos = new ArrayList<UserDto>();
		for (User user : users) {
			if (user.getRole().getRole() == 3) {
				UserDto userDto = new UserDto();
				userDto.setEmail(user.getEmail());
				userDto.setFirstName(user.getFirstName());
				userDto.setLastName(user.getLastName());
				userDtos.add(userDto);
			}
		}
		model.addAttribute("users", userDtos);
		return "securities";
	}

	@RequestMapping(value = "/addGateway", method = RequestMethod.GET)
	public ModelAndView addGateway(WebRequest request, Model model) {
		LOGGER.debug("Rendering addGateway page.");
		GatewayDto gatewayDto = new GatewayDto();
		List<String> userEmail = new ArrayList<String>();
		List<User> users = userRepo.findAll();
		for (User user : users) {
			if (checkUser(user)) {
				userEmail.add(user.getEmail());
				LOGGER.info("user Email do not have gateway {}", user.getEmail());
			}

		}
		ModelAndView mav = new ModelAndView("addGateway", "gateway", gatewayDto);
		mav.addObject("userEmails", userEmail);
		mav.addObject("mess", "");
		return mav;
	}

	@RequestMapping(value = "/addGateway", method = RequestMethod.POST)
	public ModelAndView saveGateway(WebRequest request, Model model, @ModelAttribute("gateway") GatewayDto gatewayDto) {
		LOGGER.debug("Rendering addGateway page.");

		User user = userRepo.findByEmail(gatewayDto.getUserEmail());
		List<Camera> cameras = cameraRepo.findByUser(user);
		if (!cameras.isEmpty()) {
			Gateway newGateway = new Gateway();
			newGateway.setGatewayIP(gatewayDto.getGatewayIP());
			gatewayRepo.save(newGateway);
			for (Camera camera : cameras) {
				camera.setGatewayId(newGateway.getGatewayId());
				cameraRepo.save(camera);
			}
		}

		List<Gateway> gateways = gatewayRepo.findAll();
		List<GatewayDto> gatewayDtos = new ArrayList<GatewayDto>();
		for (Gateway gateway : gateways) {
			GatewayDto gatewayDtoo = new GatewayDto();
			gatewayDtoo.setGatewayId(gateway.getGatewayId());
			gatewayDtoo.setGatewayIP(gateway.getGatewayIP());
			gatewayDtoo.setUserEmail(getUserByGateway(gateway.getGatewayId()).getEmail());
			gatewayDtos.add(gatewayDtoo);

		}

		return new ModelAndView("gateway", "gatewayDtos", gatewayDtos);

	}

	private Boolean checkUser(User user) {
		if (user.getRole().getRole() != 1) {
			return false;
		} else {
			List<Camera> cameras = cameraRepo.findByUser(user);
			for (Camera camera : cameras) {
				if (camera.getGatewayId() != 0) {
					return false;
				}
			}
		}
		return true;
	}

	@RequestMapping(value = "/gateway", method = RequestMethod.GET)
	public String getGateways(WebRequest request, Model model) {
		LOGGER.debug("Rendering securities page.");
		List<Gateway> gateways = gatewayRepo.findAll();
		List<GatewayDto> gatewayDtos = new ArrayList<GatewayDto>();
		for (Gateway gateway : gateways) {
			GatewayDto gatewayDto = new GatewayDto();
			gatewayDto.setGatewayId(gateway.getGatewayId());
			gatewayDto.setGatewayIP(gateway.getGatewayIP());
			gatewayDto.setUserEmail(getUserByGateway(gateway.getGatewayId()).getEmail());
			gatewayDtos.add(gatewayDto);

		}
		model.addAttribute("gatewayDtos", gatewayDtos);
		return "gateway";
	}

	private User getUserByGateway(int gatewayId) {
		List<Camera> cameras = cameraRepo.findByGatewayId(gatewayId);
		return cameras.get(0).getUser();
	}

	@RequestMapping(value = "/console", method = RequestMethod.GET)
	public String security(WebRequest request, Model model) {
		LOGGER.debug("Rendering registration page.");
		UserDto accountDto = new UserDto();
		model.addAttribute("user", accountDto);
		return "console";
	}

	@RequestMapping(value = "/console", method = RequestMethod.POST)
	public ModelAndView createSecurity(@ModelAttribute("user") @Valid UserDto accountDto, BindingResult result,
			WebRequest request, Errors errors, Model model) {
		LOGGER.debug("Registering user account with information: {}", accountDto);
		if (result.hasErrors()) {
			return new ModelAndView("registration", "user", accountDto);
		}

		User registered = createSecurityAccount(accountDto);
		if (registered == null) {
			result.rejectValue("email", "message.regError");
		}

		LOGGER.debug("Rendering securities page.");
		List<User> users = userRepo.findAll();
		List<UserDto> userDtos = new ArrayList<UserDto>();
		for (User user : users) {
			if (user.getRole().getRole() == 3) {
				UserDto userDto = new UserDto();
				userDto.setEmail(user.getEmail());
				userDto.setFirstName(user.getFirstName());
				userDto.setLastName(user.getLastName());
				userDtos.add(userDto);
			}
		}
		return new ModelAndView("securities", "users", userDtos);
	}

	@RequestMapping(value = "/regitrationConfirm", method = RequestMethod.GET)
	public String confirmRegistration(WebRequest request, Model model, @RequestParam("token") String token) {
		Locale locale = request.getLocale();

		VerificationToken verificationToken = service.getVerificationToken(token);
		if (verificationToken == null) {
			String message = messages.getMessage("auth.message.invalidToken", null, locale);
			model.addAttribute("message", message);
			return "redirect:/badUser.html?lang=" + locale.getLanguage();
		}

		User user = verificationToken.getUser();
		Calendar cal = Calendar.getInstance();
		if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
			model.addAttribute("message", messages.getMessage("auth.message.expired", null, locale));
			return "redirect:/badUser.html?lang=" + locale.getLanguage();
		}

		user.setEnabled(true);
		service.saveRegisteredUser(user);
		return "redirect:/login.html?lang=" + locale.getLanguage();
	}

	@RequestMapping(value = "/user/registration", method = RequestMethod.POST)
	public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserDto accountDto, BindingResult result,
			WebRequest request, Errors errors) {
		LOGGER.debug("Registering user account with information: {}", accountDto);
		if (result.hasErrors()) {
			return new ModelAndView("registration", "user", accountDto);
		}

		User registered = createUserAccount(accountDto);
		if (registered == null) {
			result.rejectValue("email", "message.regError");
		}
		try {
			String appUrl = request.getContextPath();
			eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
		} catch (Exception me) {
			return new ModelAndView("emailError", "user", accountDto);
		}
		return new ModelAndView("successRegister", "user", accountDto);
	}

	private User createUserAccount(UserDto accountDto) {
		User registered = null;
		try {
			registered = service.registerNewUserAccount(accountDto);
		} catch (EmailExistsException e) {
			return null;
		}
		return registered;
	}

	private User createSecurityAccount(UserDto accountDto) {
		User registered = null;
		try {
			registered = service.registerNewSecurityAccount(accountDto);
		} catch (EmailExistsException e) {
			return null;
		}
		return registered;
	}

}
