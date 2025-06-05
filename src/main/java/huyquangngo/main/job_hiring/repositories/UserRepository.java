package huyquangngo.main.job_hiring.repositories;

import huyquangngo.main.job_hiring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User,Long>{
    Optional<User> findByUserEmail(String userEmail);
}
