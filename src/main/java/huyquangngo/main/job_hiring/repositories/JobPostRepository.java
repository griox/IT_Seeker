package huyquangngo.main.job_hiring.repositories;

import huyquangngo.main.job_hiring.models.Employer;
import huyquangngo.main.job_hiring.models.JobPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost,Long> {
    List<JobPost> findByTitleContainingIgnoreCase(String keyword);

    List<JobPost> findByEmployer(Employer employer);
}
