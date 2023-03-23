package com.example.springcardprojectdemo.service;

import com.example.springcardprojectdemo.payload.ApiResponse;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    @Autowired
    JavaMailSender sender;

    @Autowired
    Configuration configuration;

    public ApiResponse sendText(String sendToEmail) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setText("Nimaa gap!");
            simpleMailMessage.setTo(sendToEmail);
            simpleMailMessage.setSubject("From Shaxzod");
            sender.send(simpleMailMessage);
            return new ApiResponse("Success", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("Error", false);
        }
    }

    public ApiResponse sendTextt(String sendToEmail, String text) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setText(text);
            simpleMailMessage.setTo(sendToEmail);
            simpleMailMessage.setSubject("From Shaxzod");
            sender.send(simpleMailMessage);
            return new ApiResponse("Success", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("Error", false);
        }
    }

    public ApiResponse sendHtml(String email) {
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("email", "shaxzodmurtozaqulov@gmail.com");
            model.put("fullName", "Shaxzod Murtozaqulov");
            model.put("code", "123321");
            model.put("phoneNumber", "+998999971806");

            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED);

            Template template = configuration.getTemplate("email-template.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            helper.setTo(email);
            helper.setSubject("Email verification");
            helper.setText(html, true);
            sender.send(mimeMessage);

            return new ApiResponse("Success", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("Error", false);
        }
    }

    public ApiResponse sendFile(String email) {
        try {

            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(email);
            helper.setText("File Sending");
            helper.setSubject("SpringBootApplication");
            String name = "me.png";
            File file = new File("src/main/resources/static/appFile/" + name);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = FileCopyUtils.copyToByteArray(fileInputStream);
            ByteArrayDataSource attachment = new ByteArrayDataSource(bytes, "application/octet-stream");

            helper.addAttachment(name, attachment);

            Thread thread = new Thread(){
                public void run() {
                    sender.send(mimeMessage);
                }
            };
            thread.start();
            return new ApiResponse("Success", true);
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse("Error", false);
        }
    }
}