package huyquangngo.main.job_hiring.services;

import huyquangngo.main.job_hiring.models.Application;
import huyquangngo.main.job_hiring.repositories.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ApplicationImpl implements ApplicationService{
    @Autowired
    ApplicationRepository applicationRepository;
    @Override
    public Application applyToJob(Application application) {
        application.setAppliedAt(LocalDateTime.now());
        application.setStatus("pending");
        return applicationRepository.save(application);
    }

    @Override
    public Optional<Application> getApplicationsByJobSeekerId(Long jobSeekerId) {
        return applicationRepository.findByJobSeekerId(jobSeekerId);
    }
}
