package graduationWork.server.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Transactions {

    @Id @GeneratedValue
    @Column(name = "transaction_id")
    private Long id;

    private Long timestamp;

    private String hash;

    private String from;

    private String to;

    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
