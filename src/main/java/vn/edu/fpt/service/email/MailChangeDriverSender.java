package vn.edu.fpt.service.email;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MailChangeDriverSender {

    private final JavaMailSender mailSender;

    public MailChangeDriverSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void send(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, false); // false = text

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
