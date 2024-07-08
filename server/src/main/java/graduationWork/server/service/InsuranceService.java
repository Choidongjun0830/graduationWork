package graduationWork.server.service;

import graduationWork.server.domain.Insurance;
import graduationWork.server.dto.InsuranceCreateForm;
import graduationWork.server.enumurate.InsuranceStatus;
import graduationWork.server.enumurate.InsuranceType;
import graduationWork.server.repository.InsuranceRepository;
import graduationWork.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final UserRepository userRepository;

    public Long saveInsurance(InsuranceCreateForm insuranceCreateForm) {

        Insurance insurance = new Insurance();
        insurance.setName(insuranceCreateForm.getName());
        insurance.setPremium(insuranceCreateForm.getPremium());
        insurance.setCoverageLimit(insuranceCreateForm.getCoverageLimit());
        insurance.setInsuranceType(insuranceCreateForm.getInsuranceType());

        return insuranceRepository.save(insurance);
    }

    public Insurance findOneInsurance(Long id) {
         return insuranceRepository.findById(id);
    }

    public List<Insurance> findAllInsurances() {
        return insuranceRepository.findAll();
    }

    public List<Insurance> findInsurancesByStatus(InsuranceStatus status) {
        return insuranceRepository.findAllByStatus(status);
    }

    public List<Insurance> findAllInsurancesByType(InsuranceType type) {
        return insuranceRepository.findAllOfInsuranceType(type);
    }
}
