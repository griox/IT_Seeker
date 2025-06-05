package huyquangngo.main.job_hiring.services;


import huyquangngo.main.job_hiring.models.JobPost;
import huyquangngo.main.job_hiring.repositories.JobPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class JobPostImpl implements  JobPostService{
    @Autowired
    JobPostRepository jobPostRepository;
    @Override
    public JobPost createJobPost(JobPost jobPost) {
        return jobPostRepository.save(jobPost);
    }

    @Override
    public List<JobPost> searchByKeyword(String keyword) {
        return jobPostRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @Override
    public Optional<JobPost> getJobPostById(Long id) {
        return jobPostRepository.findById(id);
    }
}
