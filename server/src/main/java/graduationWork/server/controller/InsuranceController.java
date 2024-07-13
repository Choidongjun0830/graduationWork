package graduationWork.server.controller;

import graduationWork.server.domain.Insurance;
import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.dto.CompensationApplyForm;
import graduationWork.server.dto.DelayCompensationApplyForm;
import graduationWork.server.domain.UploadFile;
import graduationWork.server.dto.UploadCompensationApplyForm;
import graduationWork.server.enumurate.InsuranceType;
import graduationWork.server.file.FileStore;
import graduationWork.server.repository.InsuranceRepository;
import graduationWork.server.service.InsuranceService;
import graduationWork.server.service.UserInsuranceService;
import graduationWork.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class InsuranceController {

    private final UserService userService;
    private final InsuranceService insuranceService;
    private final UserInsuranceService userInsuranceService;
    private final InsuranceRepository insuranceRepository;
    private final FileStore fileStore;



//    @GetMapping("/insurance/search")
//    public String search(@ModelAttribute InsuranceSearchForm form, HttpSession session) {
//        return "insurance/search";
//    }

//    @PostMapping("/insurance/search")
//    public String searchInsurance(@ModelAttribute InsuranceSearchForm form) {
//        // 검색 로직
//        model.addAttribute("searchResult", searchService.search(form));
//
//        return "searchResultPage";
//    }

    @PostMapping("/")
    public String searchInsurance(DelayCompensationApplyForm insuranceSearchForm, HttpSession session, BindingResult bindingResult) {
//        if(bindingResult.hasErrors()) {
//            return "redirect:/";
//        }
        Insurance insurance = new Insurance();

        return "insurance/insuranceCheck";
    }

    @GetMapping("/insurance/new")
    public String join() {
        return "insurance/travelTypeSelectFrom";
    }

    @GetMapping("/insurance/new/domestic")
    public String joinDomestic(Model model, HttpSession session) {
        List<Insurance> domesticInsurances = insuranceService.findAllInsurancesByType(InsuranceType.DOMESTIC);
        model.addAttribute("domesticInsurances", domesticInsurances);
        return "insurance/domesticSelect";

    }

    @GetMapping("/insurance/new/overseas")
    public String joinOverseas(Model model, HttpSession session) {
        List<Insurance> overseasInsurances = insuranceService.findAllInsurancesByType(InsuranceType.OVERSEAS);
        model.addAttribute("overseasInsurances", overseasInsurances);
        return "insurance/overseaSelect";
    }

    @GetMapping("/insurance/new/{insuranceId}")
    public String registerInsurance(@PathVariable Long insuranceId, @RequestParam InsuranceType insuranceType, Model model) {
        Insurance findInsurance = insuranceService.findOneInsurance(insuranceId);

        model.addAttribute("insurance", findInsurance);
        model.addAttribute("insuranceType", insuranceType);

        return "insurance/registerInsuranceForm";
    }

    @PostMapping("/insurance/new/{insuranceId}")
    public String registerInsuranceProc(@PathVariable Long insuranceId, @RequestParam LocalDate startDate,
                                        @RequestParam LocalDate endDate, HttpSession session, Model model) {
        //보험 가입 처리
        User loginUser = (User) session.getAttribute("loginUser");
        Long loginUserId = loginUser.getId();

        Long userInsuranceId = userInsuranceService.registerInsurance(insuranceId, loginUserId, startDate, endDate);

        session.setAttribute("userInsuranceId", userInsuranceId);

        return "redirect:/insurance/new/confirm";
    }


    @GetMapping("/insurance/new/confirm")
    public String registerConfirm(Model model, HttpSession session) {

        Long userInsuranceId = (Long) session.getAttribute("userInsuranceId");
        if(userInsuranceId == null) {
            return "redirect:/insurance/new";
        }

        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);

        model.addAttribute("userInsurance", userInsurance);

        return "insurance/registerSuccess";
    }

    @GetMapping("insurance/compensation/apply")
    public String compensationForm(@RequestParam Long userInsuranceId, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);

        CompensationApplyForm form = new CompensationApplyForm();
        form.setEmail(loginUser.getEmail());

        model.addAttribute("form", form);
        model.addAttribute("userInsurance", userInsurance);
        model.addAttribute("coverageMap", userInsurance.getInsurance().getCoverageMap());

        return "insurance/compensationApply";
    }

    @PostMapping("insurance/compensation/apply")
    public String compensationApply(@RequestParam Long userInsuranceId, @ModelAttribute CompensationApplyForm form, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        session.setAttribute("applyForm", form);
        userInsuranceService.applyFirstCompensationForm(userInsuranceId, loginUser.getId(), form);
        if (form.getReason().equals("항공기 및 수하물 지연 보상")) {
            return "redirect:/insurance/compensation/apply/flightDelay?userInsuranceId=" + userInsuranceId;
        }
        else {
            return "redirect:/insurance/compensation/apply/upload?userInsuranceId=" + userInsuranceId;
        }
    }

    @GetMapping("insurance/compensation/apply/flightDelay")
    public String flightDelayForm(@RequestParam Long userInsuranceId,
                                  @ModelAttribute("delayForm") DelayCompensationApplyForm delayForm, Model model, HttpSession session) {
        model.addAttribute("userInsuranceId", userInsuranceId);
        return "insurance/flightCompensationApply";
    }

    @PostMapping("insurance/compensation/apply/flightDelay")
    public String flightDelayApply(@RequestParam Long userInsuranceId,
                                   @ModelAttribute DelayCompensationApplyForm delayForm,
                                   HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        userInsuranceService.applyDelayCompensation(userInsuranceId, loginUser.getId(), delayForm);

        return "redirect:/user/insurances";
    }

    //파일 업로드 폼
    @GetMapping("/insurance/compensation/apply/upload")
    public String uploadForm(@RequestParam Long userInsuranceId,
                             @ModelAttribute("uploadForm") UploadCompensationApplyForm uploadForm, Model model, HttpSession session) {

        model.addAttribute("userInsuranceId", userInsuranceId);
        return "insurance/fileUploadForm";
    }

    //파일 업로드
    @PostMapping("/insurance/compensation/apply/upload")
    public String upload(@RequestParam Long userInsuranceId,
                           @ModelAttribute UploadCompensationApplyForm uploadForm,
                           HttpSession session) throws IOException {

        User loginUser = (User) session.getAttribute("loginUser");

        userInsuranceService.applyUploadCompensation(userInsuranceId, loginUser.getId(), uploadForm);

        return "redirect:/user/insurances";
    }
}
