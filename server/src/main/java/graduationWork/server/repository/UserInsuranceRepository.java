package graduationWork.server.repository;

import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserInsuranceRepository {

    private final UserRepository userRepository;
    private final InsuranceRepository insuranceRepository;
    private final EntityManager em;

    public Long save(UserInsurance userInsurance) {
        em.persist(userInsurance);
        return userInsurance.getId();
    }

    public UserInsurance findById(Long id) {
        return em.find(UserInsurance.class, id);
    }

    public List<UserInsurance> findAll() {
        return em.createQuery("select ui from UserInsurance ui", UserInsurance.class)
                .getResultList();
    }

    public List<UserInsurance> findByUser(User user) {
        Long userId = user.getId();
        return em.createQuery("select ui from UserInsurance ui where ui.user.id = :userId", UserInsurance.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
