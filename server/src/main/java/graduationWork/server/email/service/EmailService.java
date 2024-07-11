package graduationWork.server.email.service;

import graduationWork.server.domain.UserInsurance;
import graduationWork.server.enumurate.CompensationStatus;
import graduationWork.server.repository.InsuranceRepository;
import graduationWork.server.repository.UserInsuranceRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserInsuranceRepository userInsuranceRepository;
    private final InsuranceRepository insuranceRepository;

    @Value("${spring.mail.username}")
    private String sender;

    @Transactional
    public void sendCompensationEmail(Long userInsuranceId, String subject) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId);
        userInsurance.setCompensationStatus(CompensationStatus.COMPENSATED);
        String to = userInsurance.getUser().getEmail();

        try{
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(buildEmailContent(userInsurance), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private String buildEmailContent(UserInsurance userInsurance) {
        StringBuilder content = new StringBuilder();

        String reason = userInsurance.getReason();

        content.append("<div style=\"max-width: 600px; margin: auto; padding: 20px; font-family: Arial, sans-serif;\">")
                .append("<div style=\"border: 1px solid #ddd; border-radius: 10px; box-shadow: 0 0 10px rgba(0,0,0,0.1); padding: 20px;\">")
                .append("<h2 style=\"text-align: center; color: #007bff;\">보험 보상 진행을 위한 메일 전송</h2>")
                .append("<div style=\"border-top: 1px solid #ddd; padding-top: 10px;\">")
                .append("<p><strong>보험명:</strong> ").append(userInsurance.getInsurance().getName()).append("</p>")
                .append("<p><strong>회원명:</strong> ").append(userInsurance.getUser().getUsername()).append("</p>")
                .append("<p><strong>신청일:</strong> ").append(userInsurance.getApplyDate()).append("</p>")
                .append("<p><strong>신청 사유:</strong> ").append(userInsurance.getReason()).append("</p>")
                .append("<p><strong>발생일:</strong> ").append(userInsurance.getOccurrenceDate()).append("</p>")
                .append("<p><strong>보상 금액:</strong> ").append(userInsurance.getCompensationAmount()).append("</p>")
                .append("</div>")
                .append("</div>")
                .append("</div>");
        //보상 금액 이후에 수정
        return content.toString();
    }
}
