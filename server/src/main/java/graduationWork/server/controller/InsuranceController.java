package graduationWork.server.controller;

import graduationWork.server.domain.*;
import graduationWork.server.dto.CompensationApplyForm;
import graduationWork.server.dto.DelayCompensationApplyForm;
import graduationWork.server.dto.InsuranceJoinDateSelectForm;
import graduationWork.server.dto.UploadCompensationApplyForm;
import graduationWork.server.enumurate.FlightStatus;
import graduationWork.server.enumurate.InsuranceType;
import graduationWork.server.file.FileStore;
import graduationWork.server.service.FlightService;
import graduationWork.server.service.InsuranceService;
import graduationWork.server.service.UserInsuranceService;
import graduationWork.server.service.UserService;
import graduationWork.server.utils.DateTimeUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
public class InsuranceController {

    private final UserService userService;
    private final InsuranceService insuranceService;
    private final UserInsuranceService userInsuranceService;
    private final FileStore fileStore;
    private final FlightService flightService;

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
    public String registerInsurance(@PathVariable Long insuranceId,
                                    @ModelAttribute("dateSelectForm")InsuranceJoinDateSelectForm dateSelectForm,
                                    Model model) {
        Insurance findInsurance = insuranceService.findOneInsurance(insuranceId);

        model.addAttribute("insurance", findInsurance);

        return "insurance/registerInsuranceForm";
    }

    @PostMapping("/insurance/new/{insuranceId}")
    public String registerInsuranceProc(@PathVariable Long insuranceId,
                                        @SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                        @Validated @ModelAttribute("dateSelectForm")InsuranceJoinDateSelectForm dateSelectForm,
                                        BindingResult bindingResult,
                                        HttpSession session, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("insurance", insuranceService.findOneInsurance(insuranceId));
            return "insurance/registerInsuranceForm";
        }

        //보험 가입 처리
        Long loginUserId = loginUser.getId();
        LocalDate startDate = dateSelectForm.getStartDate();
        LocalDate endDate = dateSelectForm.getEndDate();

        if (startDate.isBefore(LocalDate.now()) || endDate.isBefore(LocalDate.now()) || startDate.isAfter(endDate)) {
            bindingResult.reject("insuranceJoinDateError", null);
            model.addAttribute("insurance", insuranceService.findOneInsurance(insuranceId));
            return "insurance/registerInsuranceForm";
        }

        Long userInsuranceId = userInsuranceService.joinApplyInsurance(insuranceId, loginUserId, startDate, endDate); //가입 신청

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

        return "insurance/joinApplySuccess";
    }

    //여기까지 보험 신청 아래는 보험 보상 신청//

    @GetMapping("insurance/compensation/apply")
    public String compensationForm(@RequestParam Long userInsuranceId,
                                   @ModelAttribute("form") CompensationApplyForm form,
                                   Model model,
                                   HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);

        form.setEmail(loginUser.getEmail());
        model.addAttribute("userInsurance", userInsurance);

        Map<String, String> coverageMap = userInsurance.getInsurance().getCoverageMap();
        if (coverageMap == null) {
            coverageMap = new HashMap<>();
        }
        model.addAttribute("coverageMap", coverageMap);

        return "insurance/compensationApply";
    }

    @PostMapping("insurance/compensation/apply")
    public String compensationApply(@RequestParam Long userInsuranceId,
                                    @Validated @ModelAttribute("form") CompensationApplyForm form,
                                    BindingResult bindingResult,
                                    Model model,
                                    HttpSession session) {

        if(bindingResult.hasErrors()) {
            UserInsurance userInsurance = userInsuranceService.findOne(userInsuranceId);
            model.addAttribute("userInsurance", userInsurance);
            model.addAttribute("coverageMap", userInsurance.getInsurance().getCoverageMap());
            return "insurance/compensationApply";
        }

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
                                  @ModelAttribute("delayForm") DelayCompensationApplyForm delayForm,
                                  Model model, HttpSession session) {

        model.addAttribute("userInsuranceId", userInsuranceId);
        return "insurance/flightCompensationApply";
    }

    @PostMapping("insurance/compensation/apply/flightDelay")
    public String flightDelayApply(@RequestParam Long userInsuranceId,
                                   @ModelAttribute("delayForm") @Validated DelayCompensationApplyForm delayForm,
                                   BindingResult bindingResult,
                                   Model model,
                                   HttpSession session) {

        model.addAttribute("userInsuranceId", userInsuranceId);

        String formFlightNum = delayForm.getFlightNum();
        LocalDateTime formDepartureDate = delayForm.getDepartureDate();

        if (formDepartureDate == null || formFlightNum == null) { //입력하지 않았을 때.
            bindingResult.reject("flightDelayApplyNullError",null);
            return "insurance/flightCompensationApply";
        }

        Flight findFlight = flightService.getFlight(formFlightNum, formDepartureDate);
        String dateTime = DateTimeUtils.formatDateTime(formDepartureDate);
        if (findFlight == null) { //존재하는 항공편이 없을 때.

            bindingResult.reject("flightDelayApplyError", new Object[]{formFlightNum, dateTime}, null);
            return "insurance/flightCompensationApply";
        }

        if (findFlight.getStatus() == FlightStatus.SCHEDULED) { //항공편의 상태가 지연이나 취소가 아님.
            bindingResult.reject("flightDelayApplyNotDelayedNotCancelled", new Object[]{formFlightNum, dateTime}, null);
            return "insurance/flightCompensationApply";
        }

        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "insurance/flightCompensationApply";
        }

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


    //보험 보장 내역
    @GetMapping("/insurance/details/{insuranceId}")
    public String insuranceDetails(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                   @PathVariable Long insuranceId, Model model) {

        Insurance insurance = insuranceService.findOneInsurance(insuranceId);
        model.addAttribute("insurance", insurance);

        return "insurance/insuranceDetails";
    }
}
