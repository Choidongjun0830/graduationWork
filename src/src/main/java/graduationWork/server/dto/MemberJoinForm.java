package graduationWork.server.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MemberJoinForm {

    @NotBlank(message = "{please.enter.name}")
    private String username;

    @NotBlank(message = "{please.enter.login.id}")
    private String loginId;

    @NotBlank(message = "{please.enter.password}")
    @Length(min=8, max=20)
    private String password;

    @NotBlank(message = "{please.enter.email}")
    private String email;

    private String walletAddress;

    private String zipCode;

    private String roadAddress;

    private String detailAddress;
}
