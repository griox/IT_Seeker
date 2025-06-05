package huyquangngo.main.job_hiring.controllers;

import huyquangngo.main.job_hiring.models.Application;
import huyquangngo.main.job_hiring.models.JobPost;
import huyquangngo.main.job_hiring.models.JobSeeker;
import huyquangngo.main.job_hiring.repositories.ApplicationRepository;
import huyquangngo.main.job_hiring.repositories.JobPostRepository;
import huyquangngo.main.job_hiring.repositories.JobSeekerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class ApplicationController {
    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private JobPostRepository jobPostRepository;
    @Autowired
    private JobSeekerRepository jobSeekerRepository;

    @PostMapping("/apply")
    public String applyToJob(@RequestParam("jobPostId") Long jobPostId,
                             @RequestParam("coverLetter")String coverLetter,
                             HttpSession session){
        // lay thong tin user dang dang nhap
        Long jobSeekerId = (Long) session.getAttribute("jobseekerId");
        if(jobSeekerId==null){
            return "redirect:/login";
        }
        JobPost jobPost = jobPostRepository.findById(jobPostId).orElse(null);
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId).orElse(null);
        Application application = new Application();
        application.setAppliedAt(LocalDateTime.now());
        application.setStatus("Đợi duyệt");
        application.setJobPost(jobPost);
        application.setJobSeeker(jobSeeker);
        application.setCoverLetter(coverLetter);
        applicationRepository.save(application);
        return "redirect:/job-posts";
    }
    @PostMapping("/withdraw-application/{jobPostId}")
    public String withdrawApplication(@PathVariable Long jobPostId, HttpSession session) {
        Long jobSeekerId = (Long) session.getAttribute("jobseekerId");

        // Tìm application tương ứng
        Application application = applicationRepository.findByJobSeeker_IdAndJobPost_Id(jobSeekerId, jobPostId);
        if (application != null) {
            applicationRepository.delete(application);
        }
        return "redirect:/job-posts";
    }
    @PostMapping("/applications/update-status")
    public String updateApplicationStatus(@RequestParam("applicationId") Long applicationId,
                                          @RequestParam("status") String status,
                                          @RequestParam("id") Long id,
                                          Model model,
                                          RedirectAttributes redirectAttributes) {
        Optional<Application> applicationOpt = applicationRepository.findById(applicationId);
        Optional<JobPost> optionalJobPost = jobPostRepository.findById(id);
        JobPost jobPost = optionalJobPost.get();
        model.addAttribute("jobDetail",jobPost);
        model.addAttribute("applications",applicationRepository.findByJobPost(jobPost));
        if (applicationOpt.isPresent()) {
            Application application = applicationOpt.get();
            application.setStatus(status);
            applicationRepository.save(application);
            redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thành công.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy ứng tuyển.");
        }
        return "jobsDetailForEm";
    }



}
