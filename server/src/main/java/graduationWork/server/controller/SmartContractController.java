package graduationWork.server.controller;

import graduationWork.server.domain.Transactions;
import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.dto.EtherPayReceipt;
import graduationWork.server.ether.UpbitApiClient;
import graduationWork.server.service.TransactionsService;
import graduationWork.server.service.UserInsuranceService;
import graduationWork.server.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SmartContractController {

    private final TransactionsService transactionsService;
    private final UpbitApiClient upbitApiClient;
    private final UserInsuranceService userInsuranceService;

    //유저가 보험 가입료 입금한 영수증 내역 보여주기
    @GetMapping("/user/insurances/deposit/receipt/{userInsuranceId}")
    public String showPayPremium(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                @PathVariable Long userInsuranceId, Model model) {
        String ethereumAddress = "0x0D39Cc3Efc5B7A7821F34695A877228Ae66fE52b"; //이게 그거지? 송금할 주소 무통장입금 주소

        String userWalletAddress = loginUser.getWalletAddress();
        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
        List<Transactions> userTransactions = transactionsService.findByFromAndValue(userWalletAddress, userInsurance.getEtherRegisterPrice());

        model.addAttribute("userTransactions", userTransactions);
        return "ether/depositReceipt";
    }
}
