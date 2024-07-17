package group.project.bookarchive.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import group.project.bookarchive.models.MailDTO;
import group.project.bookarchive.models.User;
import group.project.bookarchive.repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String sendFrom;

    public void sendPwdMail(MailDTO dto) {

        String tmpPwd = generateTemporaryPassword();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(tmpPwd));
        user.setTempPwd(true);
        userRepository.save(user);

        Context context = new Context();
        context.setVariable("temporaryPwd", tmpPwd);

        String body = templateEngine.process("emails/temporaryPasswordEmail", context);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(dto.getEmail());
            helper.setFrom(sendFrom);
            helper.setSubject("PagePals: Temporary Password");
            helper.setText(body, true);
            javaMailSender.send(message);

            logger.info("Temporary password email sent to: {}", dto.getEmail());

        } catch (MessagingException e) {
            logger.error("MessagingException: Failed to send email to {}: {}", dto.getEmail(), e.getMessage(), e);
        } catch (MailException e) {
            logger.error("Failed to send email to {}: {}", dto.getEmail(), e.getMessage(), e);
        }
    }

    private String generateTemporaryPassword() {
        char[] charSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder tmpPwd = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            int index = (int) (charSet.length * Math.random());
            tmpPwd.append(charSet[index]);
        }

        return tmpPwd.toString();
    }
}