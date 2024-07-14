package graduationWork.server.controller;


import graduationWork.server.domain.UploadFile;
import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.dto.InsuranceSearch;
import graduationWork.server.email.service.EmailService;
import graduationWork.server.file.FileStore;
import graduationWork.server.service.FileService;
import graduationWork.server.service.UserInsuranceService;
import graduationWork.server.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class adminController {

    private final UserService userService;
    private final UserInsuranceService userInsuranceService;
    private final EmailService emailService;
    private final FileStore fileStore;
    private final FileService fileService;

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

    @GetMapping("/insurance/admin/compensation/manage")
    public String compensationManage(@RequestParam Long userInsuranceId, Model model, HttpSession session) throws AccessDeniedException {
        checkRole(session);

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
        model.addAttribute("userInsurance", userInsurance);
        UserInsurance userInsurance1 = (UserInsurance) model.getAttribute("userInsurance");
        return "admin/compensationManage";
    }

    @PostMapping("/insurance/admin/sendCompensationMail")
    public String sendCompensationMail(@RequestParam("userInsuranceId") Long userInsuranceId, Model model, HttpSession session) throws AccessDeniedException {
        checkRole(session);

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
        userInsuranceService.setCompensationAmount(userInsuranceId);

        String sub = "보험 보상 진행을 위한 메일 전송";
        emailService.sendCompensationEmail(userInsuranceId, sub);

        model.addAttribute("message", "이메일이 성공적으로 전송되었습니다.");
        model.addAttribute("userInsurance", userInsurance);

        return "admin/compensationEmailSuccess";
    }

    @ResponseBody
    @GetMapping("/download/files/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) throws MalformedURLException {

        UploadFile uploadFile = fileService.findById(fileId);

        String storeFileName = uploadFile.getStoreFileName();
        String uploadFileName = uploadFile.getUploadFileName();

        String fullPath = fileStore.getFullPath(storeFileName);

        UrlResource resource = new UrlResource("file:" + fullPath);
        String encodeUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodeUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    public static void checkRole(HttpSession session) throws AccessDeniedException {
        User loginUser = (User) session.getAttribute("loginUser");
        if (!"ROLE_ADMIN".equals(loginUser.getRole())) {
            // 예외 처리
            throw new AccessDeniedException("Access Denied: You do not have permission to access this page.");
        }
    }
}
