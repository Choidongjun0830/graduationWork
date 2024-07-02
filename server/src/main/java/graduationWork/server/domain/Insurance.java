package graduationWork.server.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Insurance {
    @Id @GeneratedValue
    @Column(name = "insurance_id")
    private Long id;



}
