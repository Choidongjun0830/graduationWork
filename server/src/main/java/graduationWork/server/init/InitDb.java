package graduationWork.server.init;

import graduationWork.server.domain.User;
import graduationWork.server.security.PasswordEncoder;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;
        private final PasswordEncoder passwordEncoder;

        public void dbInit1() {
            User user = new User();
            user.setUsername("admin");
            user.setLoginId("adong08");
            user.setPassword(passwordEncoder.encode("adong08","1111"));
            user.setRole("ROLE_ADMIN");
            user.setEmail("dsada1234@naver.com");
            em.persist(user);
        }

        public void dbInit2() {
            User user = new User();
            user.setUsername("user");
            user.setLoginId("adong0808");
            user.setPassword(passwordEncoder.encode("adong0808","1111"));
            user.setRole("ROLE_USER");
            user.setEmail("qweqweqwe@naver.com");
            em.persist(user);
        }
    }
}
