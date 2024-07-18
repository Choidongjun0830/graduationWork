package graduationWork.server.service;

import graduationWork.server.domain.*;
import graduationWork.server.dto.*;
import graduationWork.server.enumurate.FlightStatus;
import graduationWork.server.file.FileStore;
import graduationWork.server.repository.FlightRepository;
import graduationWork.server.utils.NumberUtils;
import graduationWork.server.enumurate.CompensationOption;
import graduationWork.server.enumurate.CompensationStatus;
import graduationWork.server.enumurate.InsuranceStatus;
import graduationWork.server.repository.InsuranceRepository;
import graduationWork.server.repository.UserInsuranceRepository;
import graduationWork.server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserInsuranceService {

    private final UserInsuranceRepository userInsuranceRepository;
    private final UserRepository userRepository;
    private final InsuranceRepository insuranceRepository;
    private final FlightRepository flightRepository;
    private final FileStore fileStore;
    private final FileService fileService;

    private static final Logger logger = LoggerFactory.getLogger(UserInsuranceService.class);


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
    public void applyFirstCompensationForm(Long userInsuranceId, Long userId, CompensationApplyForm applyForm) {
        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId);

        LocalDate startDate = userInsurance.getStartDate();
        LocalDate endDate = userInsurance.getEndDate();
        LocalDate occurrenceDate = applyForm.getOccurrenceDate();

        if (occurrenceDate.isAfter(endDate) || occurrenceDate.isBefore(startDate)) {
            userInsurance.setCompensationStatus(CompensationStatus.IMPOSSIBLE);
        }

        userInsurance.setOccurrenceDate(applyForm.getOccurrenceDate());
        userInsurance.setReason(applyForm.getReason());
        userInsurance.setApplyDate(LocalDate.now());
    }

    @Transactional
    public Boolean applyDelayCompensation(Long userInsuranceId, Long userId, DelayCompensationApplyForm delayForm) {
        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId);

        CompensationOption option = delayForm.getCompensationOption();
        if(option == CompensationOption.OPTION_EMAIL) {
            userInsurance.setCompensationOption(CompensationOption.OPTION_EMAIL);
        } else {
            userInsurance.setCompensationOption(CompensationOption.OPTION_AUTO);
        }

        String flightNum = delayForm.getFlightNum();
        LocalDateTime departureDate = delayForm.getDepartureDate();

        Flight userFlight = flightRepository.findByFlightNumDepartureDate(flightNum, departureDate);
        if (userFlight == null) {
            return false;
        } else {
            if (userFlight.getStatus().equals(FlightStatus.DELAYED)) {
                if(userInsurance.getCompensationOption().equals(CompensationOption.OPTION_EMAIL)) {
                    userInsurance.setCompensationStatus(CompensationStatus.COMPENSATING);
                }
                else {
                    userInsurance.setCompensationStatus(CompensationStatus.COMPENSATED);
                }
            }
        }
        return true;
    }

    @Transactional
    public void applyUploadCompensation(Long userInsuranceId, Long userId, UploadCompensationApplyForm uploadForm) throws IOException {

        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId);

        List<MultipartFile> files = uploadForm.getInsuranceDocuments();
        List<UploadFile> uploadFileList = fileStore.storeFiles(files);

        for (UploadFile uploadFile : uploadFileList) {
            uploadFile.setUserInsurance(userInsurance);
            fileService.save(uploadFile);
        }

        userInsurance.setFiles(uploadFileList);
        userInsurance.setCompensationStatus(CompensationStatus.COMPENSATING);
        userInsurance.setCompensationOption(CompensationOption.OPTION_EMAIL);
    }

    @Transactional
    public void setCompensationAmount(Long userInsuranceId) {
        UserInsurance userInsurance = userInsuranceRepository.findById(userInsuranceId);
        String reason = userInsurance.getReason();
        log.info(reason);
        Map<String, String> details = insuranceRepository.findDetails(userInsurance.getInsurance().getId());
        log.info(details.toString());
        userInsurance.setCompensationAmount(details.get(reason));

        log.info(userInsurance.getCompensationAmount());
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

        CompensationStatus compensationStatus = insuranceSearch.getCompensationStatus();

        String insuranceName = insuranceSearch.getInsuranceName();

        String username = insuranceSearch.getUsername();

        CompensationOption option = insuranceSearch.getCompensationOption();

        return userInsuranceRepository.findAllUserInsurances(username, insuranceName, compensationStatus, option);
    }
}
