package graduationWork.server.domain;

import graduationWork.server.enumurate.CompensationStatus;
import graduationWork.server.enumurate.InsuranceStatus;
import graduationWork.server.enumurate.InsuranceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter @Setter
public class Insurance { //유저에게 가입된 보험 정보를 나타내는 용
    @Id @GeneratedValue
    @Column(name = "insurance_id")
    private Long id;

    private String name;

    //보험료
    private int premium;
    private String formattedPremium;

    //보상 한도
    private int coverageLimit;
    private String formattedCoverageLimit;

    private InsuranceType insuranceType;

    //보장 내역
    private List<String> coverageDetails;

    @Override
    public String toString() {
        return "Insurance{" +
                "name='" + name + '\'' +
                ", premium=" + premium +
                ", formattedPremium='" + formattedPremium + '\'' +
                ", coverageLimit=" + coverageLimit +
                ", formattedCoverageLimit='" + formattedCoverageLimit + '\'' +
                ", insuranceType=" + insuranceType +
                '}';
    }
}
