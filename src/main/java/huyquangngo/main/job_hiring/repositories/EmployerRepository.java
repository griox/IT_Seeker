package huyquangngo.main.job_hiring.repositories;

import huyquangngo.main.job_hiring.models.Employer;
import huyquangngo.main.job_hiring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerRepository extends JpaRepository<Employer,Long> {
    Optional<Employer> findByUser(User user);
}
