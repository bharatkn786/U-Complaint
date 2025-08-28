package com.example.demo.repository;

import com.example.demo.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // Fetch complaints of a user by their ID
    List<Complaint> findByUser_Id(Long userId);

    // Fetch all complaints along with user details
    @Query("SELECT c FROM Complaint c JOIN FETCH c.user")
    List<Complaint> findAllWithUser();

    // Fetch complaints by status
    List<Complaint> findByStatus(Complaint.Status status);

    // Optional: Fetch complaints by user & status
    List<Complaint> findByUser_IdAndStatus(Long userId, Complaint.Status status);

    // Fetch complaints by priority
    List<Complaint> findByPriority(Complaint.Priority priority);

    // Fetch complaints by status + priority (if needed)
    List<Complaint> findByStatusAndPriority(Complaint.Status status, Complaint.Priority priority);

}
