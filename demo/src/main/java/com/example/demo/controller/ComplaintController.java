package com.example.demo.controller;

import com.example.demo.dto.ComplaintRequest;
import com.example.demo.dto.UpdateStatusRequest;
import com.example.demo.dto.UpdatePriorityRequest;
import com.example.demo.model.Complaint;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ComplaintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private static final Logger logger = LoggerFactory.getLogger(ComplaintController.class);

    private final ComplaintService complaintService;
    private final UserRepository userRepository;

    public ComplaintController(ComplaintService complaintService, UserRepository userRepository) {
        this.complaintService = complaintService;
        this.userRepository = userRepository;
    }

    // ✅ Create complaint
    @PostMapping

    public Complaint createComplaint(@RequestBody ComplaintRequest request, Authentication authentication) {
        String email = authentication.getName(); // extract user from token
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Complaint complaint = Complaint.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .subcategory(request.getSubcategory())
                .location(request.getLocation())
                .contactNumber(request.getContactNumber())
                .status(Complaint.Status.NEW)
                .priority(Complaint.Priority.MEDIUM)
                .user(user)
                .build();

        return complaintService.createComplaint(complaint);
    }


    // ✅ Role-based fetching
    @GetMapping
    public List<Complaint> getComplaints(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("❌ User not found with email={}", email);
                    return new IllegalArgumentException("User not found");
                });

        logger.info("📌 Fetch complaints request by user={}, role={}", email, user.getRole());

        switch (user.getRole()) {
            case STUDENT:
                logger.debug("Returning complaints for STUDENT id={}", user.getId());
                return complaintService.getComplaintsByUser(user.getId());

            case WARDEN:
                logger.debug("Returning complaints for WARDEN categories");
                return complaintService.getComplaintsByCategories(
                        List.of("Mess", "Hostel", "Maintenance", "Transport", "Security")
                );

            case FACULTY:
                logger.debug("Returning complaints for FACULTY categories");
                return complaintService.getComplaintsByCategories(List.of("Academic"));

            case ADMIN:
                logger.debug("Returning ALL complaints for ADMIN");
                return complaintService.getAllComplaints();

            default:
                logger.error("❌ Unauthorized role access: {}", user.getRole());
                throw new SecurityException("Unauthorized role");
        }
    }

    // ✅ Get complaints by user (admin or self only)
    @GetMapping("/user/{userId}")
    public List<Complaint> getComplaintsByUser(@PathVariable Long userId, Authentication authentication) {
        String email = authentication.getName();
        User requester = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("❌ User not found with email={}", email);
                    return new IllegalArgumentException("User not found");
                });

        logger.info("📌 Fetch complaints for userId={} requested by email={}, role={}", userId, email, requester.getRole());

        if (requester.getRole() == User.Role.ADMIN || requester.getId().equals(userId)) {
            return complaintService.getComplaintsByUser(userId);
        } else {
            logger.warn("⚠️ Access denied for user={} trying to fetch complaints for userId={}", email, userId);
            throw new SecurityException("Access denied");
        }
    }

    // ✅ Update complaint status
    @PutMapping("/{id}/status")
    public Complaint updateStatus(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        logger.info("📌 Updating status for complaintId={}, newStatus={}", id, request.getStatus());
        return complaintService.updateComplaintStatus(id, request.getStatus());
    }

    // ✅ Update complaint priority
    @PutMapping("/{id}/priority")
    public Complaint updatePriority(@PathVariable Long id, @RequestBody UpdatePriorityRequest request) {
        logger.info("📌 Updating priority for complaintId={}, newPriority={}", id, request.getPriority());
        return complaintService.updateComplaintPriority(id, request.getPriority());
    }
}
