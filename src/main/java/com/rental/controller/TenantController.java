package com.rental.controller;

import com.rental.dto.Dtos.PayForm;
import com.rental.dto.Dtos.RequestForm;
import com.rental.model.Payment;
import com.rental.model.RentalRequest;
import com.rental.model.User;
import com.rental.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantController {

    private final RentalService rentals;

    @PostMapping("/requests")
    public RentalRequest request(@RequestAttribute("user") User user, @RequestBody RequestForm form) {
        return rentals.create(user, form);
    }

    @GetMapping("/requests")
    public List<RentalRequest> myRequests(@RequestAttribute("user") User user) {
        return rentals.ofTenant(user.getId());
    }

    @PostMapping("/requests/{id}/pay")
    public Payment pay(@RequestAttribute("user") User user, @PathVariable Long id,
                       @RequestBody(required = false) PayForm form) {
        return rentals.pay(user, id, form);
    }

    @GetMapping("/payments")
    public List<Payment> myPayments(@RequestAttribute("user") User user) {
        return rentals.paymentsOfTenant(user.getId());
    }
}
