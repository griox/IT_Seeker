package huyquangngo.main.job_hiring.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "employer")
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String avatar;
    private String companyName;
    private String location;

    @OneToOne
    @JoinColumn(name = "userid")
    private User user;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL)
    private List<JobPost> jobPosts;

    // getters and setters
}

