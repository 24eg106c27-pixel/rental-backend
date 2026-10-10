package com.rental.service;

import com.rental.dto.Dtos.ComplaintForm;
import com.rental.exception.ApiException;
import com.rental.exception.Check;
import com.rental.model.Complaint;
import com.rental.model.ComplaintStatus;
import com.rental.model.User;
import com.rental.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaints;

    public Complaint create(User user, ComplaintForm f) {
        Check.notBlank(f.subject(), "Subject is required");
        Check.notBlank(f.description(), "Please describe the problem");
        Complaint c = new Complaint();
        c.setRaisedBy(user);
        c.setSubject(f.subject().trim());
        c.setDescription(f.description().trim());
        c.setStatus(ComplaintStatus.OPEN);
        c.setCreatedDate(LocalDate.now());
        return complaints.save(c);
    }

    public List<Complaint> mine(Long userId) {
        return complaints.findByRaisedByIdOrderByIdDesc(userId);
    }

    public List<Complaint> all() {
        return complaints.findAllByOrderByIdDesc();
    }

    public Complaint resolve(Long id, String response) {
        Check.notBlank(response, "Write a response before resolving");
        Complaint c = complaints.findById(id).orElseThrow(() -> new ApiException(404, "Complaint not found"));
        c.setAdminResponse(response.trim());
        c.setStatus(ComplaintStatus.RESOLVED);
        c.setResolvedDate(LocalDate.now());
        return complaints.save(c);
    }
}
