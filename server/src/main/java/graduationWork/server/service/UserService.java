package graduationWork.server.service;

import graduationWork.server.domain.User;
import graduationWork.server.domain.Wallet;
import graduationWork.server.dto.PasswordUpdateForm;
import graduationWork.server.repository.UserRepository;
import graduationWork.server.security.PasswordEncoder;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Long join(User user) {

        //중복 회원 검증
        boolean IsAlreadyExists = userRepository.existsByUsername(user.getUsername());
        if (IsAlreadyExists) {
            return 0L; //에러 처리.
        }


        user.setPassword(passwordEncoder.encode(user.getLoginId(), user.getPassword()));
        user.setRole("ROLE_USER"); //ADMIN은 그냥 DB에 넣어두기
        user.setJoinDate(LocalDate.now());

        userRepository.save(user);
        return user.getId();
    }

    private User findOne(Long id) {
        return userRepository.findById(id);
    }

    private List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public boolean updatePassword(User user, PasswordUpdateForm passwordUpdateForm) {
        String findUserLoginId = user.getLoginId();

        if(!passwordEncoder.matches(findUserLoginId, passwordUpdateForm.getCurrentPassword(), user.getPassword())) {
            return false;
        }

        if(!passwordUpdateForm.getNewPassword().equals(passwordUpdateForm.getNewPasswordConfirm())) {
            return false;
        }
        String encodedNewPassword = passwordEncoder.encode(findUserLoginId, passwordUpdateForm.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public void updateWalletAccount(User user, Wallet wallet) {
        user.updateWallet(wallet);
        userRepository.save(user);
    }

//    public String getPassword() {
//        return userRepository.getPassword();
//    }
}
