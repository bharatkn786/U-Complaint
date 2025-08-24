package com.example.demo.repository;

import com.example.demo.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    // Fetch complaints of a user by their ID
    List<Complaint> findByUser_Id(Long userId);

    @Query("SELECT c FROM Complaint c JOIN FETCH c.user")
    List<Complaint> findAllWithUser();
}
