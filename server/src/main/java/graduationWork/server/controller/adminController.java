package graduationWork.server.controller;

import graduationWork.server.domain.Insurance;
import graduationWork.server.domain.UploadFile;
import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.dto.InsuranceSearch;
import graduationWork.server.email.service.EmailService;
import graduationWork.server.enumurate.CompensationStatus;
import graduationWork.server.service.UserInsuranceService;
import graduationWork.server.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class adminController {

    private final UserService userService;
    private final UserInsuranceService userInsuranceService;
    private final EmailService emailService;

    //보험 보상 신청 리스트
    @GetMapping("/insurance/admin/compensation/requests")
    public String compensationRequests(@ModelAttribute InsuranceSearch insuranceSearch, HttpSession session, Model model) throws AccessDeniedException {
        checkRole(session);

        log.info("Insurance search: " + insuranceSearch.toString());

        List<UserInsurance> userInsurances = userInsuranceService.findAllUserInsurances(insuranceSearch);

        for (UserInsurance userInsurance : userInsurances) {
            log.info(userInsurance.toString());
        }

        model.addAttribute("userInsurances", userInsurances);

        return "admin/userInsuranceListForAdmin";
    }

//    @GetMapping("/insurance/admin/compensation/download")
//    public ResponseEntity<Resource> downloadFile(@RequestParam Long userInsuranceId, @RequestParam String filename) {
//        UploadFile uploadFile = userInsuranceService.getUploadFile(userInsuranceId, filename);
//        Resource resource = fileStore.loadFileAsResource(uploadFile.getStoreFileName());
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType("application/octet-stream"))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + uploadFile.getOriginalFilename() + "\"")
//                .body(resource);
//    }


    @GetMapping("/insurance/admin/compensation/manage")
    public String compensationManage(@RequestParam Long userInsuranceId, Model model, HttpSession session) throws AccessDeniedException {
        checkRole(session);

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
        log.info(userInsurance.toString());
        model.addAttribute("userInsurance", userInsurance);
        UserInsurance userInsurance1 = (UserInsurance) model.getAttribute("userInsurance");
        log.info(userInsurance1.toString());
        return "admin/compensationManage";
    }

    @PostMapping("/insurance/admin/sendCompensationMail")
    public String sendCompensationMail(@RequestParam("userInsuranceId") Long userInsuranceId, Model model) throws MessagingException {

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
        userInsuranceService.setCompensationAmount(userInsuranceId);

        String sub = "보험 보상 진행을 위한 메일 전송";
        emailService.sendCompensationEmail(userInsuranceId, sub);

        model.addAttribute("message", "이메일이 성공적으로 전송되었습니다.");
        model.addAttribute("userInsurance", userInsurance);

        return "admin/compensationEmailSuccess";
    }

    public static void checkRole(HttpSession session) throws AccessDeniedException {
        User loginUser = (User) session.getAttribute("loginUser");
        if (!"ROLE_ADMIN".equals(loginUser.getRole())) {
            // 예외 처리
            throw new AccessDeniedException("Access Denied: You do not have permission to access this page.");
        }
    }
}
