package graduationWork.server.repository;

import graduationWork.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username) {

    }
}
