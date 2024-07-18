package graduationWork.server.controller;

import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.domain.Wallet;
import graduationWork.server.dto.PasswordUpdateForm;
import graduationWork.server.repository.InsuranceRepository;
import graduationWork.server.security.PasswordEncoder;
import graduationWork.server.service.UserInsuranceService;
import graduationWork.server.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserInsuranceService userInsuranceService;
    private final InsuranceRepository insuranceRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/join")
    public String joinForm(@ModelAttribute User user) {
        return "users/joinMemberForm";
    }

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute User user, BindingResult bindingResult, Model model) {

        String loginId = user.getLoginId();
        if(userService.findByLoginId(loginId) != null) {
            bindingResult.rejectValue("loginId", "loginId.exists", "이미 존재하는 로그인 ID 입니다.");
        }

        if(bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            return "users/joinMemberForm";
        }

        userService.join(user);
        return "redirect:/";
    }

    @GetMapping("/user/info")
    public String memberInfo(@ModelAttribute User user, HttpServletRequest request, Model model) {
        User loginUser = (User) request.getSession().getAttribute(SessionConst.LOGIN_USER);
        model.addAttribute("loginUser", loginUser);
        return "users/userInfo";
    }

    @GetMapping("/user/passwordUpdate")
    public String passwordUpdateForm(@ModelAttribute PasswordUpdateForm passwordUpdateForm) {
        return "users/passwordUpdateForm";
    }

    @PostMapping("/user/passwordUpdate") //여기서 새로운 패스워드 두번 입력 같은지 검증
    public String passwordUpdate(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser,
                                 @Validated @ModelAttribute PasswordUpdateForm passwordUpdateForm,
                                 BindingResult bindingResult) {

        String typedCurrentPassword = passwordUpdateForm.getCurrentPassword();
        String encodeTyped = passwordEncoder.encode(loginUser.getLoginId(), typedCurrentPassword);

        if(!loginUser.getPassword().equals(encodeTyped)) {
            bindingResult.rejectValue("currentPassword", "currentPassword.error");
            return "users/passwordUpdateForm";
        }

        if(!passwordUpdateForm.getNewPassword().equals(passwordUpdateForm.getNewPasswordConfirm())) {
            bindingResult.rejectValue("newPasswordConfirm","passwordUpdateError");
            return "users/passwordUpdateForm";
        }

        if(bindingResult.hasErrors()){
            return "users/passwordUpdateForm";
        }

        boolean updatePassword = userService.updatePassword(loginUser.getId(), passwordUpdateForm);
        if(updatePassword){
            return "redirect:/user/info";
        }else {
            return "users/passwordUpdateForm";
        }
    }

    @GetMapping("/user/walletAccountUpdate")
    public String walletAccountUpdateForm(@ModelAttribute Wallet wallet) {
        return "users/accountUpdateForm";
    }

    @PostMapping("/user/walletAccountUpdate")
    public String walletAccountUpdate(Wallet wallet, HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        userService.updateWalletAccount(loginUser, wallet);
        return "redirect:/user/info";
    }

    @GetMapping("/user/insurances")
    public String getUserInsurances(HttpSession session,Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        Long loginUserId = loginUser.getId();

        List<UserInsurance> userInsurances = userInsuranceService.findUserInsurances(loginUserId);
        model.addAttribute("userInsurances", userInsurances);
        return "users/insuranceListsForUser";
    }
}
