package com.example.demo.service;

import com.example.demo.model.Complaint;
import com.example.demo.repository.ComplaintRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    // Create complaint
    public Complaint createComplaint(Complaint complaint) {
        return complaintRepository.save(complaint);
    }

    // Get complaints by user
    public List<Complaint> getComplaintsByUser(Long userId) {
        return complaintRepository.findByUser_Id(userId);
    }

    // Get all complaints
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllWithUser();
    }

    // Update complaint status
    public Complaint updateComplaintStatus(Long id, Complaint.Status status) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaint.setStatus(status);
        return complaintRepository.save(complaint);
    }
}
