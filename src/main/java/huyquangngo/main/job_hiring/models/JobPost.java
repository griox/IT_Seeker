package huyquangngo.main.job_hiring.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name ="jobpost" )
public class JobPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private Double salary;
    private String jobType;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private Employer employer;

    @OneToMany(mappedBy = "jobPost", cascade = CascadeType.ALL)
    private List<Application> applications;

    // getters and setters
}
