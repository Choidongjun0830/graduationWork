package graduationWork.server.controller;

import graduationWork.server.domain.Insurance;
import graduationWork.server.dto.InsuranceRequestForm;
import graduationWork.server.dto.InsuranceSearchForm;
import graduationWork.server.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class InsuranceController {

    private final UserService userService;

//    @GetMapping("/insurance/search")
//    public String search(@ModelAttribute InsuranceSearchForm form, HttpSession session) {
//        return "insurance/search";
//    }

//    @PostMapping("/insurance/search")
//    public String searchInsurance(@ModelAttribute InsuranceSearchForm form) {
//        // 검색 로직을 여기에 작성
//        // 예: 검색 결과를 모델에 추가
//        model.addAttribute("searchResult", searchService.search(form));
//
//        // 검색 결과를 보여줄 페이지로 이동
//        return "searchResultPage"; // 템플릿 이름을 여기에 적절히 변경
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
    public String joinDomestic(@ModelAttribute InsuranceRequestForm form, HttpSession session) {
        return "insurance/insuranceJoinDomesticForm";

    }

    @GetMapping("/insurance/new/overseas")
    public String joinOverseas(@ModelAttribute InsuranceRequestForm form, HttpSession session) {
        return "insurance/insuranceJoinOverseaForm";
    }

    @GetMapping("/insurance/requests")
    public String insuranceRequests(@ModelAttribute Insurance insurances, HttpSession session) {
        return "board/insuranceRequests";
    }


}
