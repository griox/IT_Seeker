package huyquangngo.main.job_hiring.repositories;

import huyquangngo.main.job_hiring.models.Application;
import huyquangngo.main.job_hiring.models.JobPost;
import huyquangngo.main.job_hiring.models.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application,Long> {

    Optional<Application> findByJobSeekerId (Long jobSeekerId);
    List<Application> findByJobSeeker(JobSeeker jobSeeker);
    List<Application> findByJobPost(JobPost jobPost);
    Application findByJobSeeker_IdAndJobPost_Id(Long jobSeekerId, Long jobPostId);

}
