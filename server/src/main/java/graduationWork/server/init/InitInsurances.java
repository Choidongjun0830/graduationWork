package graduationWork.server.init;

import graduationWork.server.domain.Insurance;
import graduationWork.server.enumurate.InsuranceType;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitInsurances {

    private final InitInsuranceService initInsuranceService;

    @PostConstruct
    public void init() {
        initInsuranceService.domesticInit1();
        initInsuranceService.domesticInit2();
        initInsuranceService.overseaInit1();
        initInsuranceService.overseaInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitInsuranceService {

        private final EntityManager em;

        public void domesticInit1() {
            Insurance insurance = new Insurance();
            insurance.setName("국내 여행-Basic");
            insurance.setPremium(10000);
            insurance.setFormattedPremium("10,000원");
            insurance.setCoverageLimit(1000000);
            insurance.setFormattedCoverageLimit("1,000,000원");
            insurance.setInsuranceType(InsuranceType.DOMESTIC);
            em.persist(insurance);
        }
        public void domesticInit2() {
            Insurance insurance = new Insurance();
            insurance.setName("국내 여행-Premium");
            insurance.setPremium(15000);
            insurance.setFormattedPremium("15,000원");
            insurance.setCoverageLimit(3000000);
            insurance.setFormattedCoverageLimit("3,000,000원");
            insurance.setInsuranceType(InsuranceType.DOMESTIC);
            em.persist(insurance);
        }
        public void overseaInit1() {
            Insurance insurance = new Insurance();
            insurance.setName("해외 여행-Basic");
            insurance.setPremium(15000);
            insurance.setFormattedPremium("15,000원");
            insurance.setCoverageLimit(1000000);
            insurance.setFormattedCoverageLimit("1,000,000원");
            insurance.setInsuranceType(InsuranceType.OVERSEAS);
            em.persist(insurance);
        }
        public void overseaInit2() {
            Insurance insurance = new Insurance();
            insurance.setName("해외 여행-Premium");
            insurance.setPremium(20000);
            insurance.setFormattedPremium("20,000원");
            insurance.setCoverageLimit(3000000);
            insurance.setFormattedCoverageLimit("3,000,000원");
            insurance.setInsuranceType(InsuranceType.OVERSEAS);
            em.persist(insurance);
        }
    }

}
