package com.appsdeveloper.app.ws.service.impl;

import com.appsdeveloper.app.ws.service.HtmlMail;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service("htmlMail")
public class HtmlMailImpl implements HtmlMail {
    private JavaMailSender mailSender;
    public HtmlMailImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    @Override
    public void sendMail(String from, String to, String subject, String token) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            String htmlMsg = "<h3>To confirm your account</h3>"
                    +"<a href='http://localhost:8080/verification-service/email-veritification.html?token=" + token + "'> please click here </a><br/>"
                    +"<p>Thank you</p>";

            message.setSubject(subject);
            MimeMessageHelper helper;
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setText(htmlMsg, true);
            mailSender.send(message);
            System.out.println("Email sent!");
        } catch (MessagingException ex) {
            Logger.getLogger(HtmlMail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
