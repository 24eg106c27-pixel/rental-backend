package com.rental.controller;

import com.rental.dto.Dtos.ComplaintForm;
import com.rental.model.Complaint;
import com.rental.model.User;
import com.rental.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// owners and tenants raise complaints and follow their status here
@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaints;

    @PostMapping
    public Complaint create(@RequestAttribute("user") User user, @RequestBody ComplaintForm form) {
        return complaints.create(user, form);
    }

    @GetMapping
    public List<Complaint> mine(@RequestAttribute("user") User user) {
        return complaints.mine(user.getId());
    }
}
