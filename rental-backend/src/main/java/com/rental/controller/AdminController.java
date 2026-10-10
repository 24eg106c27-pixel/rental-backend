package com.rental.controller;

import com.rental.dto.Dtos.ResolveForm;
import com.rental.model.Complaint;
import com.rental.model.Payment;
import com.rental.model.Property;
import com.rental.model.User;
import com.rental.service.AdminService;
import com.rental.service.ComplaintService;
import com.rental.service.PropertyService;
import com.rental.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService admin;
    private final PropertyService properties;
    private final RentalService rentals;
    private final ComplaintService complaints;

    @GetMapping("/report")
    public Map<String, Object> report() {
        return admin.report();
    }

    @GetMapping("/users")
    public List<User> users() {
        return admin.users();
    }

    @PutMapping("/users/{id}/verify")
    public User verify(@PathVariable Long id, @RequestParam boolean verified) {
        return admin.setVerified(id, verified);
    }

    @GetMapping("/properties")
    public List<Property> properties() {
        return properties.all();
    }

    @PutMapping("/properties/{id}/status")
    public Property status(@PathVariable Long id, @RequestParam String value) {
        return properties.setListingStatus(id, value);
    }

    @GetMapping("/payments")
    public List<Payment> payments() {
        return rentals.allPayments();
    }

    @GetMapping("/complaints")
    public List<Complaint> complaints() {
        return complaints.all();
    }

    @PutMapping("/complaints/{id}/resolve")
    public Complaint resolve(@PathVariable Long id, @RequestBody ResolveForm form) {
        return complaints.resolve(id, form.response());
    }
}
