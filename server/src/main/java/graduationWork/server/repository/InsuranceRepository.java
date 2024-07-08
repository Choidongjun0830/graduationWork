package graduationWork.server.repository;

import graduationWork.server.domain.Insurance;
import graduationWork.server.enumurate.InsuranceStatus;
import graduationWork.server.enumurate.InsuranceType;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 보험 저장
 * 보험 id로 검색
 * 보험 가입 유저로 검색
 * 관리자용 검색 기능
 */

@Repository
@RequiredArgsConstructor
public class InsuranceRepository {

    private final EntityManager em;

    @Transactional
    public Long save(Insurance insurance) {
        em.persist(insurance);
        return insurance.getId();
    }

    public Insurance findById(Long id) {
        return em.find(Insurance.class, id);
    }

    public List<Insurance> findAllOfUser(Long userId) {
        return em.createQuery("select i from Insurance i where i.user.id = :userId")
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Insurance> findAll() {
        return em.createQuery("select i from Insurance i", Insurance.class).getResultList();
    }

    public List<Insurance> findAllOfInsuranceType(InsuranceType insuranceType) {
        return em.createQuery("select i from Insurance i where i.insuranceType = :insuranceType")
                .setParameter("insuranceType", insuranceType)
                .getResultList();
    }

    public List<Insurance> findAllByStatus(InsuranceStatus status) {
        return em.createQuery("select i from Insurance i where i.status = :status")
                .setParameter("status", status)
                .getResultList();
    }

    //동적 쿼리로 해야할듯 ByStatus 이런건..
}
