package se.alipsa.renjin.webreports.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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



  public void send(String to, String subject, String message) {
    if ("fakehost".equals(mailServerHost)) {
      LOG.info("email disabled, printing message instead of sending it");
      LOG.info("To: {}, Subject: {}, Message: {}", to, subject, message);
      return;
    }
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(fromAddress);
    msg.setTo(to);
    msg.setSubject(subject);
    msg.setText(message);
    emailSender.send(msg);
  }
}
