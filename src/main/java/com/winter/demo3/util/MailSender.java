package com.winter.demo3.util;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;

@Service
public class MailSender implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
    private JavaMailSenderImpl mailSender;

    @Autowired
    private VelocityEngine velocityEngine;

    public boolean sendWithHTMLTemplate(String to,String subject,
                                        String template,Map<String,Object> model){
        try{
            String nick = MimeUtility.encodeText("673401080");
            InternetAddress from = new InternetAddress("673401080@qq.com");
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
//            String result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine,template,"UTF-8",model);//方法被弃置

            // instead of a model map, you use a VelocityContext

            VelocityContext velocityContext = new VelocityContext();
            for(Map.Entry<String, Object> entry: model.entrySet()){
                velocityContext.put(entry.getKey(),entry.getValue());
            }

            // the velocityEngine you wired into Spring has a mergeTemplate function
            // you can use to do the same thing as VelocityEngineUtils.mergeTemplate
            // with the exception that it uses a writer instead of returning a String

            StringWriter stringWriter = new StringWriter();
            velocityEngine.mergeTemplate(template, "UTF-8", velocityContext, stringWriter);

            // this is assuming you're sending HTML email using MimeMessageHelper



            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setSubject(subject);
//            mimeMessageHelper.setText(result,true);
            mimeMessageHelper.setText(stringWriter.toString(), true);//替换的方法
            mailSender.send(mimeMessage);
            return true;
        }catch(Exception e){
            logger.error("发送邮件失败"+e.getMessage());
            return false;
        }
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername("yuandongxu869@qq.com");
        mailSender.setPassword("rgjkgswecjnbbbeg");
        mailSender.setHost("smtp.qq.com");
        mailSender.setPort(465);//465
        mailSender.setProtocol("smtps");
        mailSender.setDefaultEncoding("utf8");
        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.ssl.enable",true);
        javaMailProperties.put("mail.smtp.auth",true);
        javaMailProperties.put("mail.smtp.timeout",25000);
        javaMailProperties.put("mail.debug",true);
        mailSender.setJavaMailProperties(javaMailProperties);
    }
}
