package com.rental.service;

import com.rental.dto.Dtos.PayForm;
import com.rental.dto.Dtos.RequestForm;
import com.rental.exception.ApiException;
import com.rental.exception.Check;
import com.rental.model.*;
import com.rental.repository.PaymentRepository;
import com.rental.repository.PropertyRepository;
import com.rental.repository.RentalRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RentalService {

    private final RentalRequestRepository requests;
    private final PropertyRepository properties;
    private final PaymentRepository payments;

    // ---------- tenant ----------
    @Transactional
    public RentalRequest create(User tenant, RequestForm f) {
        Check.that(f.propertyId() != null, 400, "Select a property");
        Property p = properties.findById(f.propertyId())
                .orElseThrow(() -> new ApiException(404, "Property not found"));
        Check.that(p.getListingStatus() == ListingStatus.VERIFIED && p.isAvailable(), 409,
                "This property is not available for rent");
        Check.that(f.moveInDate() != null && !f.moveInDate().isBefore(LocalDate.now()), 400,
                "Choose a move-in date from today onwards");
        int months = (f.months() == null) ? 1 : f.months();
        Check.that(months >= 1 && months <= 36, 400, "Months must be between 1 and 36");
        Check.that(!requests.existsByTenantIdAndPropertyIdAndStatusIn(tenant.getId(), p.getId(),
                List.of(RequestStatus.PENDING, RequestStatus.APPROVED)), 409,
                "You already have an active request for this property");

        RentalRequest r = new RentalRequest();
        r.setProperty(p);
        r.setTenant(tenant);
        r.setMessage(f.message());
        r.setMoveInDate(f.moveInDate());
        r.setMonths(months);
        r.setTotalAmount(p.getRent() * months);
        r.setStatus(RequestStatus.PENDING);
        r.setRequestDate(LocalDate.now());
        return requests.save(r);
    }

    public List<RentalRequest> ofTenant(Long tenantId) {
        return requests.findByTenantIdOrderByIdDesc(tenantId);
    }

    @Transactional
    public Payment pay(User tenant, Long requestId, PayForm form) {
        RentalRequest r = requests.findById(requestId)
                .filter(x -> x.getTenant().getId().equals(tenant.getId()))
                .orElseThrow(() -> new ApiException(404, "Request not found"));
        Check.that(r.getStatus() == RequestStatus.APPROVED, 409, "Only approved requests can be paid");

        Payment pay = new Payment();
        pay.setRentalRequest(r);
        pay.setTenant(tenant);
        pay.setAmount(r.getTotalAmount());
        pay.setPaymentDate(LocalDate.now());
        String method = (form == null || form.method() == null || form.method().isBlank())
                ? "ONLINE" : form.method().trim().toUpperCase();
        pay.setMethod(method.length() > 20 ? "ONLINE" : method);
        pay.setReference("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        r.setStatus(RequestStatus.PAID);
        requests.save(r);
        return payments.save(pay);
    }

    public List<Payment> paymentsOfTenant(Long tenantId) {
        return payments.findByTenantIdOrderByIdDesc(tenantId);
    }

    // ---------- owner ----------
    public List<RentalRequest> ofOwner(Long ownerId) {
        return requests.findByPropertyOwnerIdOrderByIdDesc(ownerId);
    }

    @Transactional
    public RentalRequest decide(User owner, Long requestId, boolean approve) {
        RentalRequest r = requests.findById(requestId)
                .filter(x -> x.getProperty().getOwner().getId().equals(owner.getId()))
                .orElseThrow(() -> new ApiException(404, "Request not found"));
        Check.that(r.getStatus() == RequestStatus.PENDING, 409, "This request is already " + r.getStatus());

        LocalDate today = LocalDate.now();
        if (approve) {
            Property p = r.getProperty();
            Check.that(p.isAvailable(), 409, "This property is no longer available");
            r.setStatus(RequestStatus.APPROVED);
            r.setDecisionDate(today);
            requests.save(r);

            // the property is now rented, so other waiting requests are rejected
            p.setAvailable(false);
            properties.save(p);
            for (RentalRequest other : requests.findByPropertyIdAndStatus(p.getId(), RequestStatus.PENDING)) {
                if (!other.getId().equals(r.getId())) {
                    other.setStatus(RequestStatus.REJECTED);
                    other.setDecisionDate(today);
                    requests.save(other);
                }
            }
            return r;
        }
        r.setStatus(RequestStatus.REJECTED);
        r.setDecisionDate(today);
        return requests.save(r);
    }

    public List<Payment> paymentsOfOwner(Long ownerId) {
        return payments.findByRentalRequestPropertyOwnerIdOrderByIdDesc(ownerId);
    }

    // ---------- admin ----------
    public List<Payment> allPayments() {
        return payments.findAllByOrderByIdDesc();
    }
}
