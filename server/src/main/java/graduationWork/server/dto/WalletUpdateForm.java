package graduationWork.server.dto;

import graduationWork.server.domain.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class WalletUpdateForm {

    @Id @GeneratedValue
    @Column(name = "wallet_id")
    private Long id;

    private String accountAddress;

}
