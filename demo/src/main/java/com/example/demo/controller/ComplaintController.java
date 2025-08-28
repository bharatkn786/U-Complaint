package com.example.demo.controller;

import com.example.demo.model.Complaint;
import com.example.demo.model.User;
import com.example.demo.service.ComplaintService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/complaints")
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private UserService userService;

    // Students create a complaint
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public Complaint createComplaint(@RequestBody Complaint complaint,
                                     Authentication authentication) {
        String email = authentication.getName();
        User user = userService.getUserByEmail(email); // ✅ fetch user
        complaint.setUser(user);
        return complaintService.createComplaint(complaint);
    }

    // Students see their complaints
    @GetMapping("/my/{userId}")
    @PreAuthorize("hasRole('STUDENT')")
    public List<Complaint> getMyComplaints(@PathVariable Long userId) {
        return complaintService.getComplaintsByUser(userId);
    }

    // Admin/Warden view all complaints
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    // Warden/Admin update complaint status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    public Complaint updateStatus(@PathVariable Long id,
                                  @RequestParam Complaint.Status status) {
        return complaintService.updateComplaintStatus(id, status);
    }

    // ✅ Warden/Admin update complaint priority
    @PutMapping("/{id}/priority")
    @PreAuthorize("hasAnyRole('ADMIN','WARDEN')")
    public Complaint updatePriority(@PathVariable Long id,
                                    @RequestParam Complaint.Priority priority) {
        return complaintService.updateComplaintPriority(id, priority);
    }
}
