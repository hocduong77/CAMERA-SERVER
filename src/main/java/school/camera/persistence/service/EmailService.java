package school.camera.persistence.service;

import java.util.List;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.opencv.core.Mat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

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

public class EmailService implements Runnable {

	public IImageRepo imageRepo;
	public IVideoRepo videoRepo;
	public CameraRepo cameraRepo;
	public Thread thread;
	public Integer notificationId;
	public JavaMailSender mailSender;
	public INotificationRepo notificationRepo;
	public UserRepository userRepo;

	@Override
	public void run() {
		try {
			Notification motification = notificationRepo.findOne(notificationId);
			Camera camera = cameraRepo.findOne(motification.getCameraId());
			User security = null;
			if (camera.getSecurityId() != null) {
				security = userRepo.findOne(camera.getSecurityId());
			}
			List<Video> videos = videoRepo.findByNotificationId(notificationId);
			List<Image> images = imageRepo.findByNotificationId(notificationId);
			User user = (User) camera.getUser();
			String userName = user.getEmail();
			System.out.println("user email" + userName);
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			mimeMessage.setSubject("CAMERA-SERVER");
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(userName));
			/*
			 * String conntent = "<video width=\"400\" controls>" +
			 * "<source src=\"" + videos.get(0).getVideoUrl() +
			 * "\" type=\"video/mp4\">" + "</video>"; conntent += "<br>"; for
			 * (Image image : images) { conntent += "<img src=\"" +
			 * image.getImageUrl() + "\" style=\"width:304px;height:228px;\" >";
			 * } System.out.println(conntent);
			 */
			String content = "<p> video motion detection </p>";
			content += videos.get(0).getVideoUrl();
			content += "<br>";
			if (images.size() >= 1) {
				content += "<p> face detection </p>";
			}
			for (Image image : images) {
				content += "<br>";
				content += image.getImageUrl();
			}
			mimeMessage.setContent(content, "text/html");

			// send to security user.
			mailSender.send(mimeMessage);
			if (security != null) {
				mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(security.getEmail()));
				mailSender.send(mimeMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

}
