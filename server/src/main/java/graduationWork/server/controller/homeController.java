package graduationWork.server.controller;

import graduationWork.server.domain.User;
import graduationWork.server.dto.DelayCompensationApplyForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class homeController {

    @GetMapping("/")
    public String homeLogin(@SessionAttribute(name = SessionConst.LOGIN_USER, required = false) User loginUser, Model model) {

        DelayCompensationApplyForm flightCompensationApplyForm = new DelayCompensationApplyForm();
        model.addAttribute("flightCompensationApplyForm", flightCompensationApplyForm);

        if(loginUser == null) {
            return "home";
        }

        model.addAttribute("user", loginUser);


        if (loginUser.getRole().equals("ROLE_ADMIN")) {
            return "adminHome";
        } else {
            return "userHome";
        }
    }
}
