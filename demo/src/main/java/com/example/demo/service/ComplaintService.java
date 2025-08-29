package com.example.demo.service;

import com.example.demo.model.Complaint;
import com.example.demo.repository.ComplaintRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintService {

    private static final Logger logger = LoggerFactory.getLogger(ComplaintService.class);

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    // ✅ Create new complaint
    public Complaint createComplaint(Complaint complaint) {
        logger.info("Creating complaint for userId={}, title={}",
                complaint.getUser().getId(), complaint.getTitle());
        return complaintRepository.save(complaint);
    }

    // ✅ Get complaints by user
    public List<Complaint> getComplaintsByUser(Long userId) {
        logger.info("Fetching complaints for userId={}", userId);
        return complaintRepository.findByUser_Id(userId);
    }

    // ✅ Get all complaints (with user details)
    public List<Complaint> getAllComplaints() {
        logger.info("Fetching all complaints with user details");
        return complaintRepository.findAllWithUser();
    }

    // ✅ Get complaints by categories (for Warden & Faculty)
    public List<Complaint> getComplaintsByCategories(List<String> categories) {
        logger.info("Fetching complaints by categories={}", categories);
        return complaintRepository.findByCategoryIn(categories);
    }

    // ✅ Get single complaint by ID (safe lookup)
    public Complaint getComplaintById(Long id) {
        logger.debug("Fetching complaint by id={}", id);
        return complaintRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Complaint not found with ID={}", id);
                    return new IllegalArgumentException("❌ Complaint not found with ID: " + id);
                });
    }

    // ✅ Update complaint status
    public Complaint updateComplaintStatus(Long id, Complaint.Status status) {
        logger.info("Updating complaint id={} with status={}", id, status);
        Complaint complaint = getComplaintById(id);
        complaint.setStatus(status);
        return complaintRepository.save(complaint);
    }

    // ✅ Update complaint priority
    public Complaint updateComplaintPriority(Long id, Complaint.Priority priority) {
        logger.info("Updating complaint id={} with priority={}", id, priority);
        Complaint complaint = getComplaintById(id);
        complaint.setPriority(priority);
        return complaintRepository.save(complaint);
    }

    // ✅ Delete complaint
    public void deleteComplaint(Long id) {
        logger.warn("Deleting complaint id={}", id);
        if (!complaintRepository.existsById(id)) {
            logger.error("Cannot delete. Complaint not found with ID={}", id);
            throw new IllegalArgumentException("❌ Cannot delete. Complaint not found with ID: " + id);
        }
        complaintRepository.deleteById(id);
    }
}
