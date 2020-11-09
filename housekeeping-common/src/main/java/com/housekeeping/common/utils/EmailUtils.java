package com.housekeeping.common.utils;

import cn.hutool.core.util.CharsetUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author su
 * @create 2020/11/9 15:13
 */
public class EmailUtils {

    private static JavaMailSender mailSender;

    @Autowired
    public void setMailSender(JavaMailSender mailSender){
        EmailUtils.mailSender = mailSender;
    }

    public static void sendCodeToValidationEmail(String receiverMail, Map map, String subject) {
        try {
            //发送邮件。。。
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom("HouseKeeping");
            helper.setTo(receiverMail);
            helper.setSubject(subject);

            VelocityEngine ve = new VelocityEngine();
            //可选值："class"--从classpath中读取，"file"--从文件系统中读取
            ve.setProperty("resource.loader", "class");
            //如果从文件系统中读取模板，那么属性值为org.apache.velocity.runtime.resource.loader.FileResourceLoader
            ve.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader" +
                    ".ClasspathResourceLoader");
            ve.init();
            Template t = ve.getTemplate("mailTemplate/validationEmail.vm", CharsetUtil.UTF_8);
            VelocityContext context = new VelocityContext(map);
            //渲染模板
            StringWriter sw = new StringWriter();
            t.merge(context, sw);
            helper.setText(sw.toString(), true);
            System.out.println("-----");
            System.out.println(sw.toString());
            System.out.println("-----");
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.out.println("html格式邮件发送失败");
        }
    }
}
