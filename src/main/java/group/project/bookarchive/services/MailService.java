package group.project.bookarchive.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import group.project.bookarchive.models.MailDTO;

@Service
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String SendFrom;

    public void sendPwdMail(MailDTO dto) {

        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String tmpPwd = "";

        int index = 0;
        for (int i = 0; i < 10; i++) {
            index = (int) (charSet.length * Math.random());
            tmpPwd += charSet[index];
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }
            message.setTo(dto.getEmail());
            message.setFrom(SendFrom);
            message.setText("Hello,\n"
                    + "A request has been received to generate the temporary password for your account.\n"
                    + "Please log in to the PagePals using this temporary password."
                    + "Once logged in, make sure to change your password immediately."
                    + "Below you can find your temporary password.\n"
                    + "Your temporary password: " + tmpPwd);
            message.setSubject("PagePals: Temporary Password");
            javaMailSender.send(message);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid email address: " + e.getMessage());
        } catch (MailParseException e) {
            e.printStackTrace();
        } catch (MailAuthenticationException e) {
            e.printStackTrace();
        } catch (MailSendException e) {
            e.printStackTrace();
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
}
