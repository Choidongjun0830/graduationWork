package graduationWork.server.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateForm {

    //    @Length(min=8, max=20)
    private String currentPassword;
    //    @Length(min=8, max=20)
    private String newPassword;
    //    @Length(min=8, max=20)
    private String newPasswordConfirm;


    @Override
    public String toString() {
        return "PasswordUpdateForm{" +
                "currentPassword='" + currentPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", newPasswordConfirm='" + newPasswordConfirm + '\'' +
                '}';
    }
}
