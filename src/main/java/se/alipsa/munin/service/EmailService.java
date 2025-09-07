package se.alipsa.munin.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service for sending emails, both plain text and HTML.
 * It uses JavaMailSender to send emails and can be configured to disable email sending
 * by setting the mail server host to "fakehost".
 */
@Service
public class EmailService {

  private final JavaMailSender emailSender;
  private static Logger LOG = LoggerFactory.getLogger(EmailService.class);

  @Value("${spring.mail.host:fakehost}")
  String mailServerHost;

  @Value("${munin.email.from:admin@localhost}")
  String fromAddress;

  /**
   * Constructor with autowired dependencies.
   *
   * @param emailSender the JavaMailSender to use for sending emails
   */
  @Autowired
  public EmailService(JavaMailSender emailSender) {
    this.emailSender = emailSender;
  }


  /**
   * Sends a plain text email.
   *
   * @param subject the subject of the email
   * @param message the body of the email
   * @param to      the recipient email addresses
   */
  public void sendText(String subject, String message, String... to) {
    if (checkAndHandleFakehost(subject, message, to)) return;
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setFrom(fromAddress);
    msg.setTo(to);
    msg.setSubject(subject);
    msg.setText(message);
    emailSender.send(msg);
  }

  /**
   * Sends an HTML email.
   *
   * @param subject the subject of the email
   * @param message the body of the email in HTML format
   * @param to      the recipient email addresses
   * @throws MessagingException if there is an error creating or sending the email
   */
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

  /**
   * Checks if the mail server host is set to "fakehost" and handles the email accordingly.
   * If it is "fakehost", it logs the email details instead of sending it.
   *
   * @param subject the subject of the email
   * @param message the body of the email
   * @param to      the recipient email addresses
   * @return true if the email sending is disabled (i.e., mail server host is "fakehost"), false otherwise
   */
  private boolean checkAndHandleFakehost(String subject, String message, String[] to) {
    if ("fakehost".equals(mailServerHost)) {
      LOG.info("email disabled, printing message instead of sending it");
      LOG.info("To: {}, Subject: {}, Message: {}", to, subject, message);
      return true;
    }
    return false;
  }
}
