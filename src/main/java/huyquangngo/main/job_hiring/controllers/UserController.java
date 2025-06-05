package huyquangngo.main.job_hiring.controllers;

import huyquangngo.main.job_hiring.models.Employer;
import huyquangngo.main.job_hiring.models.JobSeeker;
import huyquangngo.main.job_hiring.models.Role;
import huyquangngo.main.job_hiring.models.User;
import huyquangngo.main.job_hiring.repositories.EmployerRepository;
import huyquangngo.main.job_hiring.repositories.JobSeekerRepository;
import huyquangngo.main.job_hiring.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmployerRepository employerRepository;

    @Autowired
    private JobSeekerRepository jobSeekerRepository;
    // Xử lý đăng ký
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model,
                               @RequestParam(value = "cvFile", required = false) MultipartFile cvFile,
                               @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) {
        try {
            // Kiểm tra email trùng lặp
            Optional<User> existing = userService.getUserByEmail(user.getUserEmail());
            if (existing.isPresent()) {
                model.addAttribute("error", "Email đã được sử dụng.");
                return "register";
            }
            // Lưu user
            userService.registerUser(user);
            System.out.println("User saved: " + user.getUserId());

            String filename = null;
            if (user.getRole() == Role.Employer) {
                if (avatarFile != null && !avatarFile.isEmpty()) {
                    String uploadDir = new File("src/main/resources/static/img/avatar").getAbsolutePath();
                    filename = UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();
                    File saveFile = new File(uploadDir, filename);
                    saveFile.getParentFile().mkdirs();
                    avatarFile.transferTo(saveFile);
                }
                Employer employer = new Employer();
                employer.setAvatar(filename != null ? "img/avatar/" + filename : null);
                employer.setCompanyName(user.getCompanyName());
                employer.setLocation(user.getLocation());
                employer.setUser(user);
                employerRepository.save(employer);
                System.out.println("Employer saved: " + employer.getId());
            } else if (user.getRole() == Role.Job_seeker) {
                if (cvFile != null && !cvFile.isEmpty()) {
                    String uploadDir = new File("src/main/resources/static/upload").getAbsolutePath();
                    filename = UUID.randomUUID() + "_" + cvFile.getOriginalFilename();
                    File saveFile = new File(uploadDir, filename);
                    saveFile.getParentFile().mkdirs();
                    cvFile.transferTo(saveFile);
                }
                JobSeeker jobSeeker = new JobSeeker();
                jobSeeker.setCvUrl(filename != null ? "upload/" + filename : null);
                jobSeeker.setUser(user);
                jobSeekerRepository.save(jobSeeker);
                System.out.println("JobSeeker saved: " + jobSeeker.getId());
            }

            model.addAttribute("success", "Đăng ký thành công! Hãy đăng nhập.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", "Đăng ký thất bại: " + e.getMessage());
            e.printStackTrace();
            return "register";
        }
    }

    @GetMapping("/profile")
    public String showProfileForm(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("Employer".equals(userRole)) {
            Employer employer = employerRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Employer not found"));
            user.setAvatar(employer.getAvatar());
            user.setCompanyName(employer.getCompanyName());
            user.setLocation(employer.getLocation());
        } else if ("Job_seeker".equals(userRole)) {
            JobSeeker jobSeeker = jobSeekerRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("JobSeeker not found"));
            user.setCvUrl(jobSeeker.getCvUrl());
        }

        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                                @RequestParam(value = "cvFile", required = false) MultipartFile cvFile,
                                HttpSession session, Model model) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            String userRole = (String) session.getAttribute("userRole");

            User existingUser = userService.getUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update general user info
            existingUser.setUserName(updatedUser.getUserName());

            String filename = null;
            if ("Employer".equals(userRole)) {
                Employer employer = employerRepository.findByUser(existingUser)
                        .orElseThrow(() -> new RuntimeException("Employer not found"));

                // Handle avatar file upload
                if (avatarFile != null && !avatarFile.isEmpty()) {
                    String uploadDir = new File("src/main/resources/static/img/avatar").getAbsolutePath();
                    filename = UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();
                    File saveFile = new File(uploadDir, filename);
                    saveFile.getParentFile().mkdirs();
                    avatarFile.transferTo(saveFile);
                    employer.setAvatar("img/avatar/" + filename);
                }

                // Update employer details
                employer.setCompanyName(updatedUser.getCompanyName());
                employer.setLocation(updatedUser.getLocation());
                employerRepository.save(employer);
            } else if ("Job_seeker".equals(userRole)) {
                JobSeeker jobSeeker = jobSeekerRepository.findByUser(existingUser)
                        .orElseThrow(() -> new RuntimeException("JobSeeker not found"));

                // Handle CV file upload
                if (cvFile != null && !cvFile.isEmpty()) {
                    String uploadDir = new File("src/main/resources/static/upload").getAbsolutePath();
                    filename = UUID.randomUUID() + "_" + cvFile.getOriginalFilename();
                    File saveFile = new File(uploadDir, filename);
                    saveFile.getParentFile().mkdirs();
                    cvFile.transferTo(saveFile);
                    jobSeeker.setCvUrl("upload/" + filename);
                }

                jobSeekerRepository.save(jobSeeker);
            }
            // Save updated user
            userService.updateUser(existingUser);

            // Update session attributes
            session.setAttribute("userName", existingUser.getUserName());
            if ("Employer".equals(userRole)) {
                Employer employer = employerRepository.findByUser(existingUser).orElse(null);
                if (employer != null) {
                    session.setAttribute("avatar", employer.getAvatar());
                    session.setAttribute("companyName", employer.getCompanyName());
                    session.setAttribute("location", employer.getLocation());
                }
            } else if ("Job_seeker".equals(userRole)) {
                JobSeeker jobSeeker = jobSeekerRepository.findByUser(existingUser).orElse(null);
                if (jobSeeker != null) {
                    session.setAttribute("cvUrl", jobSeeker.getCvUrl());
                }
            }

            model.addAttribute("success", "Profile updated successfully!");
            return "redirect:/profile";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update profile: " + e.getMessage());
            e.printStackTrace();
            return "profile";
        }
    }
}
