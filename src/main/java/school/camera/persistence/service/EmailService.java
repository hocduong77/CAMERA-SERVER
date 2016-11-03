package school.camera.persistence.service;

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

public class EmailService implements Runnable{

	public CameraRepo cameraRepo;
	public IImageRepo imageRepo;
	public Long cameraId;
	public Thread thread;
	public Integer notificationId;
	public MessageSource messages;
	public JavaMailSender mailSender;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		SimpleMailMessage sender = new SimpleMailMessage();
		MimeMessage message = ((JavaMailSender) sender).createMimeMessage();
		
         
	}

}
