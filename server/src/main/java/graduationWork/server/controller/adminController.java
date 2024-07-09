package graduationWork.server.controller;

import graduationWork.server.domain.Insurance;
import graduationWork.server.domain.User;
import graduationWork.server.dto.InsuranceSearch;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
@Slf4j
public class adminController {

    //보험 보상 신청 리스트
    @GetMapping("/insurance/admin/compensation/requests")
    public String compensationRequests(@ModelAttribute InsuranceSearch insuranceSearch, HttpSession session) {
        checkRole(session);
        return "insurance/userInsuranceListForAdmin";
    }

    private static void checkRole(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser.getRole() != "ROLE_ADMIN") {
            //예외 처리
        }
    }
}
