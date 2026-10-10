package com.rental.controller;

import com.rental.model.Payment;
import com.rental.model.Property;
import com.rental.model.RentalRequest;
import com.rental.model.User;
import com.rental.service.PropertyService;
import com.rental.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final PropertyService properties;
    private final RentalService rentals;

    @GetMapping("/properties")
    public List<Property> myProperties(@RequestAttribute("user") User user) {
        return properties.ofOwner(user.getId());
    }

    @PostMapping("/properties")
    public Property add(@RequestAttribute("user") User user, @RequestBody Property property) {
        return properties.add(user, property);
    }

    @PutMapping("/properties/{id}")
    public Property update(@RequestAttribute("user") User user, @PathVariable Long id, @RequestBody Property property) {
        return properties.update(user, id, property);
    }

    @PutMapping("/properties/{id}/availability")
    public Property availability(@RequestAttribute("user") User user, @PathVariable Long id,
                                 @RequestParam boolean available) {
        return properties.setAvailable(user, id, available);
    }

    @DeleteMapping("/properties/{id}")
    public void delete(@RequestAttribute("user") User user, @PathVariable Long id) {
        properties.delete(user, id);
    }

    @GetMapping("/requests")
    public List<RentalRequest> requests(@RequestAttribute("user") User user) {
        return rentals.ofOwner(user.getId());
    }

    @PutMapping("/requests/{id}/approve")
    public RentalRequest approve(@RequestAttribute("user") User user, @PathVariable Long id) {
        return rentals.decide(user, id, true);
    }

    @PutMapping("/requests/{id}/reject")
    public RentalRequest reject(@RequestAttribute("user") User user, @PathVariable Long id) {
        return rentals.decide(user, id, false);
    }

    @GetMapping("/payments")
    public List<Payment> payments(@RequestAttribute("user") User user) {
        return rentals.paymentsOfOwner(user.getId());
    }
}
