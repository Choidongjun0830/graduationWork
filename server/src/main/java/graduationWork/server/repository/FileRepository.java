package graduationWork.server.repository;

import graduationWork.server.domain.UploadFile;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FileRepository {

    private final EntityManager em;

    public Long save(UploadFile file) {
        em.persist(file);
        return file.getId();
    }
}
