package graduationWork.server.domain;

import graduationWork.server.enumurate.CompensationStatus;
import graduationWork.server.enumurate.InsuranceStatus;
import graduationWork.server.enumurate.InsuranceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter @Setter
public class Insurance { //유저에게 가입된 보험 정보를 나타내는 용
    @Id @GeneratedValue
    @Column(name = "insurance_id")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    //보험 가입 날짜
    private LocalDate registerDate;

    //보험 시작 날짜
    private LocalDate startDate;

    //보험 만료일
    private LocalDate endDate;

    //보험 상태 (활성, 만료, 취소) 유저한테 보여주는 것
    @Enumerated(EnumType.STRING)
    private InsuranceStatus status;

    //보험료
    private BigDecimal premium;

    //보상 한도
    private BigDecimal coverageLimit;

    //보상 상태 (가능, 불가능, 보상중, 보상됨) //이건 다른 엔티티로 가야함.
    @Enumerated(EnumType.STRING)
    private CompensationStatus compensationStatus;

    private InsuranceType insuranceType;

    //보험사 -> 이것도 엔티티 만들어서 조인 형태로 해야할 거 같음.
//    private String provider;

    //지급 내역 -> 이건 그냥 다른 엔티티 만들어서 페이지 만들기(주문 내역처럼)
}
