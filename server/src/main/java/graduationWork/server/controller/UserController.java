package graduationWork.server.controller;

import graduationWork.server.domain.User;
import graduationWork.server.domain.Wallet;
import graduationWork.server.dto.PasswordUpdateForm;
import graduationWork.server.repository.UserRepository;
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

@Controller
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/join")
    public String joinForm(@ModelAttribute User user) {
        log.info("Join Controller");
        return "users/joinMemberForm";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute User user, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
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

    @PostMapping("/user/passwordUpdate")
    public String passwordUpdate(PasswordUpdateForm passwordUpdateForm, HttpSession session, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "users/userInfo";
        }


        User loginUser = (User) session.getAttribute("loginUser");
        boolean updatePassword = userService.updatePassword(loginUser, passwordUpdateForm);
        if(updatePassword){
            return "redirect:/user/info";
        }else {
            return "redirect:/";
        }
    }

    @GetMapping("/user/walletAccountUpdate")
    public String walletAccountUpdateForm(@ModelAttribute Wallet wallet) {
        return "users/accountUpdateForm";
    }

    @PostMapping("/user/walletAccountUpdate")
    public String walletAccountUpdate(Wallet wallet, HttpSession session, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "users/userInfo";
        }

        User loginUser = (User) session.getAttribute("loginUser");
        userService.updateWalletAccount(loginUser, wallet);
        return "redirect:/user/info";
    }
}
