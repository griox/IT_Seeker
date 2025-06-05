package huyquangngo.main.job_hiring.controllers;

import huyquangngo.main.job_hiring.models.*;
import huyquangngo.main.job_hiring.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class JobPostController {

    @Autowired
    private JobPostRepository jobPostRepository;

    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobSeekerRepository jobSeekerRepository;
    @Autowired
    private ApplicationRepository applicationRepository;
    @GetMapping("/employerJobs")
    public String showOwnJobPost(Model model, HttpSession session){
        Long employerId = (Long) session.getAttribute("employerId");
        Employer employer = employerRepository.findById(employerId).orElse(null);
        List<JobPost> jobPosts = jobPostRepository.findByEmployer(employer);
        model.addAttribute("jobPosts",jobPosts);
        return "jobsForEm";
    }
    @GetMapping("/jobCreate")
    public String showJobPostForm(Model model, HttpSession session) {
        // Get logged-in user details from session
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        // Get employer details
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Employer employer = employerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        // Add employer details and new JobPost to model
        model.addAttribute("employer", employer);
        model.addAttribute("jobPost", new JobPost());
        return "Job_post";
    }
    @GetMapping("/job-posts")
    public String showAllJobPosts(Model model, HttpSession session) {
        List<JobPost> jobPosts = jobPostRepository.findAll();
        model.addAttribute("jobPosts", jobPosts);

        Long jobSeekerid = (Long) session.getAttribute("jobseekerId");
            JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerid).orElse(null);
            // Tạo map chứa ID job đã ứng tuyển
            Set<Long> appliedJobIds = applicationRepository.findByJobSeeker(jobSeeker)
                    .stream()
                    .map(app -> app.getJobPost().getId())
                    .collect(Collectors.toSet());

            model.addAttribute("appliedJobIds", appliedJobIds);


        return "jobs";
    }
    @GetMapping("/showResult")
    public String showResult(HttpSession session, Model model){
        Long jobSeekerId = (Long) session.getAttribute("jobseekerId");
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId).orElse(null);
        if (jobSeekerId == null) {
            return "redirect:/login"; // hoặc trang thông báo
        }

        List<Application> applicationList = applicationRepository.findByJobSeeker(jobSeeker);
        model.addAttribute("applicationList",applicationList);
        return "result";
    }
    @GetMapping("/go-to-job-posts")
    public String redirectToJobs(HttpSession session, RedirectAttributes redirectAttributes) {
        String userRole = (String) session.getAttribute("userRole");

        if ("Employer".equals(userRole)) {
            redirectAttributes.addFlashAttribute("message", "Bạn phải đăng nhập bằng tài khoản ứng viên để xem việc làm.");
            return "redirect:/index";
        }
        return "redirect:/job-posts";
    }
    @GetMapping("/go-to-post-job")
    public String redirectToPostJob(HttpSession session, RedirectAttributes redirectAttributes) {
        String userRole = (String) session.getAttribute("userRole");

        if ("Job_seeker".equals(userRole)) {
            redirectAttributes.addFlashAttribute("message", "Bạn phải đăng nhập bằng tài khoản Employer để đăng tuyển dụng.");
            return "redirect:/index";
        }

        return "redirect:/jobCreate";
    }
    @PostMapping("/job-post/create")
    public String createJobPost(@ModelAttribute JobPost jobPost, HttpSession session) {
        // Get logged-in user details from session
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        // Check if user is an Employer
        if (userId == null || !"Employer".equals(userRole)) {
            return "redirect:/login";
        }

        // Get employer details
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Employer employer = employerRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Employer not found"));

        // Set job post details
        jobPost.setEmployer(employer);
        jobPost.setCreatedAt(LocalDateTime.now());

        // Save job post
        jobPostRepository.save(jobPost);

        return "redirect:/index";
    }
    @GetMapping("/job-details/{id}")
    public String showJobDetails(@PathVariable("id") Long jobPostId, Model model, HttpSession session){
        Optional<JobPost> optionalJobPost = jobPostRepository.findById(jobPostId);
        JobPost jobPost = optionalJobPost.get();
        model.addAttribute("jobDetail",jobPost);
        Long jobSeekerid = (Long) session.getAttribute("jobseekerId");
        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerid).orElse(null);
        // Tạo map chứa ID job đã ứng tuyển
        Set<Long> appliedJobIds = applicationRepository.findByJobSeeker(jobSeeker)
                .stream()
                .map(app -> app.getJobPost().getId())
                .collect(Collectors.toSet());

        model.addAttribute("appliedJobIds", appliedJobIds);

        return "job_details";
    }
    @GetMapping("/job-details-forem/{id}")
    public String showJobDetailsForEm(@PathVariable("id") Long jobPostId, Model model, HttpSession session){
        Optional<JobPost> optionalJobPost = jobPostRepository.findById(jobPostId);
        JobPost jobPost = optionalJobPost.get();
        model.addAttribute("jobDetail",jobPost);
        model.addAttribute("applications",applicationRepository.findByJobPost(jobPost));
        return "jobsDetailForEm";
    }
    @PostMapping("/update-post/{id}")
    public String updateJobPost(@PathVariable Long id, @ModelAttribute("jobDetail") JobPost updatedjobPost, Model model){
        JobPost exsistingJobPost = jobPostRepository.findById(id).orElse(null);
        exsistingJobPost.setCreatedAt(LocalDateTime.now());
        exsistingJobPost.setJobType(updatedjobPost.getJobType());
        exsistingJobPost.setSalary(updatedjobPost.getSalary());
        exsistingJobPost.setTitle(updatedjobPost.getTitle());
        exsistingJobPost.setDescription(updatedjobPost.getDescription());
        jobPostRepository.save(exsistingJobPost);
        model.addAttribute("jobDetail",exsistingJobPost);
        model.addAttribute("applications",applicationRepository.findByJobPost(exsistingJobPost));
        return "jobsDetailForEm";
    }

    @PostMapping("/job-post/delete/{id}")
    public String deleteJobPost(@PathVariable Long id) {
        jobPostRepository.deleteById(id);
        return "redirect:/employerJobs";
    }
}