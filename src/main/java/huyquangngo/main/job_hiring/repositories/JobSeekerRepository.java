package huyquangngo.main.job_hiring.repositories;

import huyquangngo.main.job_hiring.models.JobSeeker;
import huyquangngo.main.job_hiring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobSeekerRepository extends JpaRepository<JobSeeker,Long> {
    Optional<JobSeeker> findByUser(User user);
}
