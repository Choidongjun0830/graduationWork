package graduationWork.server.domain;

import graduationWork.server.validation.SaveCheck;
import graduationWork.server.validation.UpdateCheck;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Slf4j
public class User {

    @Id @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "로그인ID는 필수 입니다.")
    private String loginId;

    @Column(nullable = false)
    @NotBlank(message = "비밀번호는 필수 입니다.")
//    @Length(min=8, max=20)
    private String password;

    @NotBlank(message = "이름은 필수 입니다.")
    private String username;

    @NotBlank(message = "이메일은 필수 입니다.")
    private String email;

    private String role = "USER";
    private LocalDate joinDate;

    @Embedded
//    @NotEmpty(message = "주소는 필수 입니다.")
    private Address address;

    private String walletAddress;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<UserInsurance> userInsurances = new ArrayList<>();

    //==연관 관계 편의 메서드==//

    public void addUserInsurance(UserInsurance userInsurance) {
        userInsurances.add(userInsurance);
        userInsurance.setUser(this);
    }
}
