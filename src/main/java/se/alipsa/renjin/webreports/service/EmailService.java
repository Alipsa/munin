package se.alipsa.renjin.webreports.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {

  private final JavaMailSender emailSender;
  private static Logger LOG = LoggerFactory.getLogger(EmailService.class);

  @Value("${spring.mail.host:fakehost}")
  String mailServerHost;

  @Value("${webreports.email.from:admin@localhost}")
  String fromAddress;

  @Autowired
  public EmailService(JavaMailSender emailSender) {
    this.emailSender = emailSender;
  }



  public void sendText(String subject, String message, String... to) {
    if (checkAndHandleFakehost(subject, message, to)) return;
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(fromAddress);
    msg.setTo(to);
    msg.setSubject(subject);
    msg.setText(message);
    emailSender.send(msg);
  }

  public void sendHtml(String subject, String message, String... to) throws MessagingException {
    if (checkAndHandleFakehost(subject, message, to)) return;
    MimeMessage mimeMsg = emailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(mimeMsg, false);
    helper.setSubject(subject);
    helper.setText(message, true);
    helper.setTo(to);
    helper.setFrom(fromAddress);
    emailSender.send(mimeMsg);
  }

  private boolean checkAndHandleFakehost(String subject, String message, String[] to) {
    if ("fakehost".equals(mailServerHost)) {
      LOG.info("email disabled, printing message instead of sending it");
      LOG.info("To: {}, Subject: {}, Message: {}", to, subject, message);
      return true;
    }
    return false;
  }
}
