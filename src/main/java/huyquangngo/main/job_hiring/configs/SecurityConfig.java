package huyquangngo.main.job_hiring.configs;

import huyquangngo.main.job_hiring.models.Employer;
import huyquangngo.main.job_hiring.models.JobSeeker;
import huyquangngo.main.job_hiring.models.Role;
import huyquangngo.main.job_hiring.models.User;
import huyquangngo.main.job_hiring.repositories.EmployerRepository;
import huyquangngo.main.job_hiring.repositories.JobSeekerRepository;
import huyquangngo.main.job_hiring.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // Use NoOpPasswordEncoder for plain-text passwords
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomerDetailsService customerDetailsService, EmployerRepository employerRepository, JobSeekerRepository jobSeekerRepository) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/register", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("userEmail")
                        .passwordParameter("userPassword")
                        .successHandler((request, response, authentication) -> {
                            UserDetails userDetails = (UserDetails)  authentication.getPrincipal();
                            // Lấy user từ customerDetailsService
                            User user = customerDetailsService.getUserByEmail(userDetails.getUsername());
                            // lưu vào session
                            request.getSession().setAttribute("userId",user.getUserId());
                            request.getSession().setAttribute("userEmail",user.getUserEmail());
                            request.getSession().setAttribute("userName",user.getUserName());
                            request.getSession().setAttribute("userRole",user.getRole().name());
                            if(user.getRole() == Role.Employer){
                                Employer employer = employerRepository.findByUser(user)
                                                .orElseThrow(()-> new RuntimeException("Employer not found"));
                                request.getSession().setAttribute("avatar",employer.getAvatar());
                                request.getSession().setAttribute("companyName", employer.getCompanyName());
                                request.getSession().setAttribute("location", employer.getLocation());
                                request.getSession().setAttribute("employerId",user.getEmployer().getId());
                                response.sendRedirect("/index");
                            }else{
                                JobSeeker jobseeker = jobSeekerRepository.findByUser(user)
                                                .orElseThrow(()-> new RuntimeException("Không thể tìm thấy ứng viên"));
                                request.getSession().setAttribute("cvURl",user.getCvUrl());
                                request.getSession().setAttribute("jobseekerId",user.getJobSeeker().getId());
                                response.sendRedirect("/index");
                            }
                        })
//                                .defaultSuccessUrl("/index",true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Endpoint xử lý đăng xuất
                        .logoutSuccessUrl("/login") // Chuyển hướng sau khi đăng xuất
                        .invalidateHttpSession(true) // Hủy session
                        .deleteCookies("JSESSIONID") // Xóa cookie session
                        .permitAll());
        http.userDetailsService(customerDetailsService);
        return http.build();
    }
}