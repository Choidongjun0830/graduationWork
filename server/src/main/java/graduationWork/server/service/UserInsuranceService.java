package graduationWork.server.service;

import graduationWork.server.domain.Insurance;
import graduationWork.server.domain.NumberUtils;
import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.dto.CompensationApplyForm;
import graduationWork.server.dto.InsuranceSearch;
import graduationWork.server.enumurate.CompensationOption;
import graduationWork.server.enumurate.CompensationStatus;
import graduationWork.server.enumurate.InsuranceStatus;
import graduationWork.server.repository.InsuranceRepository;
import graduationWork.server.repository.UserInsuranceRepository;
import graduationWork.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserInsuranceService {

    private final UserInsuranceRepository userInsuranceRepository;
    private final UserRepository userRepository;
    private final InsuranceRepository insuranceRepository;

    @Transactional
    public Long registerInsurance(Long insuranceId, Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId);

        UserInsurance userInsurance = new UserInsurance();

        userInsurance.setUser(user);

        Insurance findInsurance = insuranceRepository.findById(insuranceId);

        userInsurance.setInsurance(findInsurance);

        userInsurance.setStartDate(startDate);
        userInsurance.setEndDate(endDate);
        userInsurance.setRegisterDate(LocalDate.now());
        userInsurance.setStatus(InsuranceStatus.ACTIVE);
        userInsurance.setCompensationStatus(CompensationStatus.NOT_SUBMITTED);

        user.addUserInsurance(userInsurance);

        Period period = Period.between(startDate, endDate);
        int days = period.getDays();
        int premium = findInsurance.getPremium();
        int registerPrice = premium * days;
        userInsurance.setRegisterPrice(NumberUtils.formatCurrency(registerPrice));

        return userInsuranceRepository.save(userInsurance);
    }

    @Transactional
    public Boolean applyCompensation(Long userInsuranceId, Long userId, CompensationApplyForm form) {
        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId);

        LocalDate startDate = userInsurance.getStartDate();
        LocalDate endDate = userInsurance.getEndDate();
        LocalDate occurrenceDate = form.getOccurrenceDate();

        if((occurrenceDate.isBefore(endDate) || occurrenceDate.isEqual(endDate)) && (occurrenceDate.isAfter(startDate) || occurrenceDate.isEqual(startDate))) {
            CompensationOption option = form.getCompensationOption();
            if(option == CompensationOption.OPTION_EMAIL) {
                userInsurance.setCompensationStatus(CompensationStatus.COMPENSATING);
            } else if (option == CompensationOption.OPTION_AUTO) {
                userInsurance.setCompensationStatus(CompensationStatus.COMPENSATED);
            }
        } else{
            userInsurance.setCompensationStatus(CompensationStatus.IMPOSSIBLE);
            return false;
        }
        return true;
    }

    public UserInsurance findOne(Long id) {
        return userInsuranceRepository.findById(id);
    }

    public List<UserInsurance> findUserInsurances(Long userId) {
        User user = userRepository.findById(userId);
        return user.getUserInsurances();
    }

    public List<UserInsurance> findAll() {
        return userInsuranceRepository.findAll();
    }

    public List<UserInsurance> findAllUserInsurances(InsuranceSearch insuranceSearch) {
        return userInsuranceRepository.findAllUserInsurances(insuranceSearch);
    }
}
