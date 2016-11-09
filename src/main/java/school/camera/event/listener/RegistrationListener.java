package school.camera.event.listener;

import java.util.UUID;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import school.camera.event.OnRegistrationCompleteEvent;
import school.camera.persistence.model.User;
import school.camera.persistence.service.IUserService;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    @Autowired
    private IUserService service;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
    	
    	
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationTokenForUser(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = event.getAppUrl() + "/regitrationConfirm.html?token=" + token;
        String message = messages.getMessage("message.regSucc", null, event.getLocale());
        
        try {
        	 MimeMessage mimeMessage = mailSender.createMimeMessage();
             mimeMessage.setSubject(subject);
             mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientAddress));
             // Set From: header field of the header.
             mimeMessage.setContent(message + " \r\n" + "http://localhost:8080" + confirmationUrl, "text/html");
             mailSender.send(mimeMessage);
		} catch (Exception e) {
			// TODO: handle exception
		}
       
       
    }
}
