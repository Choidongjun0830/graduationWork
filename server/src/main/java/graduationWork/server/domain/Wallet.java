package graduationWork.server.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Wallet {

    @Id @GeneratedValue
    @Column(name = "wallet_id")
    private Long id;

    private String accountNum;

    @OneToOne(mappedBy = "wallet")
    private User user;

    @Override
    public String toString() {
        return "Wallet{" +
                ", accountNum='" + accountNum + '\'' +
                ", user=" + user +
                '}';
    }
}
