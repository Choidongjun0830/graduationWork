package graduationWork.server.service;

import graduationWork.server.domain.Insurance;
import graduationWork.server.enumurate.InsuranceStatus;
import graduationWork.server.repository.InsuranceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InsuranceService {

    private final InsuranceRepository insuranceRepository;

    private Long saveInsurance(Insurance insurance) {

        return insuranceRepository.save(insurance);
    }

    private Insurance findOneInsurance(Long id) {
         return insuranceRepository.findById(id);
    }

    private List<Insurance> findAllInsurances() {
        return insuranceRepository.findAll();
    }

    private List<Insurance> findInsurancesByStatus(InsuranceStatus status) {
        return insuranceRepository.findAllByStatus(status);
    }
}
