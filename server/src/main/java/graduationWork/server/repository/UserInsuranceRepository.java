package graduationWork.server.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import graduationWork.server.domain.QUserInsurance;
import graduationWork.server.domain.User;
import graduationWork.server.domain.UserInsurance;
import graduationWork.server.dto.InsuranceSearch;
import graduationWork.server.enumurate.CompensationOption;
import graduationWork.server.enumurate.CompensationStatus;
import graduationWork.server.enumurate.InsuranceType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static graduationWork.server.domain.QInsurance.insurance;
import static graduationWork.server.domain.QUser.user;
import static graduationWork.server.domain.QUserInsurance.userInsurance;

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

    public List<UserInsurance> findAllUserInsurances(String usernameCond, String insuranceNameCond, CompensationStatus  statusCond, CompensationOption optionCond) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return queryFactory
                .selectFrom(userInsurance)
                .join(userInsurance.insurance, insurance)
                .join(userInsurance.user, user)
                .where(
                        usernameEq(usernameCond),
                        insuranceNameEq(insuranceNameCond),
                        compensationOptionEq(optionCond),
                        compensationStatusEq(statusCond)
                        )
                .fetch();
    }

    private BooleanExpression usernameEq(String usernameCond) {
        return usernameCond != null ? user.username.eq(usernameCond) : null;
    }

    private BooleanExpression insuranceNameEq(String insuranceNameCond) {
        return insuranceNameCond != null ? insurance.name.eq(insuranceNameCond) : null;
    }

    private BooleanExpression compensationOptionEq(CompensationOption compensationOptionCond) {
        return compensationOptionCond != null ? userInsurance.compensationOption.eq(compensationOptionCond) : null;
    }

    private BooleanExpression compensationStatusEq(CompensationStatus statusCond) {
        return statusCond != null ? userInsurance.compensationStatus.eq(statusCond) : null;
    }
}
