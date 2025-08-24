package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now(); // auto-update timestamp
    }

    // ✅ Link complaint to User (STUDENT role will create complaints)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "authorities"})
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    public enum Status {
        NEW, IN_PROGRESS, RESOLVED
    }
}
