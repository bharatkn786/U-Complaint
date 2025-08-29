package com.example.demo.dto;

import com.example.demo.model.Complaint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintRequest {
    private String title;
    private String description;
    private String category;
    private String subcategory;
    private String location;
    private String contactNumber;
    private Long userId; // user making complaint

    // ✅ Optional: allow passing from frontend
    private Complaint.Status status;
    private Complaint.Priority priority;
}
