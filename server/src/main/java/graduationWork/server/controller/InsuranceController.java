package graduationWork.server.controller;

import graduationWork.server.domain.Insurance;
import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.dto.CompensationApplyForm;
import graduationWork.server.dto.InsuranceSearchForm;
import graduationWork.server.enumurate.InsuranceType;
import graduationWork.server.repository.InsuranceRepository;
import graduationWork.server.service.InsuranceService;
import graduationWork.server.service.UserInsuranceService;
import graduationWork.server.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
    public String searchInsurance(InsuranceSearchForm insuranceSearchForm, HttpSession session, BindingResult bindingResult) {
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
        log.info(userInsurance.toString());

        model.addAttribute("userInsurance", userInsurance);

        return "insurance/registerSuccess";
    }

    @GetMapping("insurance/compensation/apply")
    public String compensationApply(@ModelAttribute CompensationApplyForm form, HttpSession session) {
        return "insurance/compensationApply";
    }



}
