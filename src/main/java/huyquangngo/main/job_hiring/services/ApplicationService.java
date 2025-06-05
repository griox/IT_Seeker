package huyquangngo.main.job_hiring.services;

import huyquangngo.main.job_hiring.models.Application;

import java.util.Optional;

public interface ApplicationService {
    Application applyToJob(Application application);
    Optional<Application> getApplicationsByJobSeekerId(Long jobSeekerId);
}
