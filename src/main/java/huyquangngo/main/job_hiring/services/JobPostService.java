package huyquangngo.main.job_hiring.services;

import huyquangngo.main.job_hiring.models.JobPost;

import java.util.List;
import java.util.Optional;

public interface JobPostService {
    JobPost createJobPost(JobPost jobPost);
    List<JobPost> searchByKeyword(String keyword);
    Optional<JobPost> getJobPostById(Long id);
}
