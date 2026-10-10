package com.rental.service;

import com.rental.exception.ApiException;
import com.rental.exception.Check;
import com.rental.model.*;
import com.rental.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository users;
    private final PropertyRepository properties;
    private final RentalRequestRepository requests;
    private final PaymentRepository payments;
    private final ComplaintRepository complaints;

    // numbers for the reports page
    public Map<String, Object> report() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("totalUsers", users.count());
        m.put("owners", users.countByRole(Role.OWNER));
        m.put("tenants", users.countByRole(Role.TENANT));
        m.put("totalProperties", properties.count());
        m.put("verifiedProperties", properties.countByListingStatus(ListingStatus.VERIFIED));
        m.put("pendingProperties", properties.countByListingStatus(ListingStatus.PENDING));
        m.put("rentedProperties", properties.countByAvailableAndListingStatus(false, ListingStatus.VERIFIED));
        m.put("totalRequests", requests.count());
        m.put("pendingRequests", requests.countByStatus(RequestStatus.PENDING));
        m.put("approvedRequests", requests.countByStatus(RequestStatus.APPROVED));
        m.put("paidRequests", requests.countByStatus(RequestStatus.PAID));
        m.put("transactions", payments.count());
        m.put("revenue", payments.totalAmount());
        m.put("openComplaints", complaints.countByStatus(ComplaintStatus.OPEN));
        return m;
    }

    public List<User> users() {
        return users.findAllByOrderByIdDesc();
    }

    public User setVerified(Long id, boolean verified) {
        User u = users.findById(id).orElseThrow(() -> new ApiException(404, "User not found"));
        Check.that(u.getRole() != Role.ADMIN, 400, "The administrator account cannot be changed");
        u.setVerified(verified);
        return users.save(u);
    }
}
