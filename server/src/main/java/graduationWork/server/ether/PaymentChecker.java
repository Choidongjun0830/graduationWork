package graduationWork.server.ether;

import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.email.service.EmailService;
import graduationWork.server.enumurate.InsuranceStatus;
import graduationWork.server.service.TransactionsService;
import graduationWork.server.service.UserInsuranceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentChecker {

    private final EtherscanApiClient etherscanApiClient;
    private final UserInsuranceService userInsuranceService;
    private final TransactionsService transactionsService;
    private final EmailService emailService;

//    @Scheduled(fixedRate = 18000) //3분마다 실행
    public void checkPayment() {
        checkUserPayments();
    }

    private void checkUserPayments() {
        List<UserInsurance> allPendingUserInsurances = userInsuranceService.findAllPendingUserInsurances();
        for (UserInsurance userInsurance : allPendingUserInsurances) {
            String from = userInsurance.getUser().getWalletAddress();
            String etherRegisterPrice = userInsurance.getEtherRegisterPrice();

            boolean paymentReceived = etherscanApiClient.checkPayment(from, Double.parseDouble(etherRegisterPrice));

            if (paymentReceived) {
                //거래 상태 업데이트
                userInsurance.setStatus(InsuranceStatus.JOINED);
                //트랜잭션 테이블 생각해보기

                notifyUser(userInsurance.getId());
            }
        }
    }

    private void notifyUser(Long userInsuranceId) {
        String sub = "보험 가입 완료";
        emailService.sendJoinEmail(userInsuranceId, sub);
    }
}
