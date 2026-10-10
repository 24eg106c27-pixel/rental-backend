package com.rental.service;

import com.rental.exception.ApiException;
import com.rental.exception.Check;
import com.rental.model.ListingStatus;
import com.rental.model.Property;
import com.rental.model.User;
import com.rental.repository.PropertyRepository;
import com.rental.repository.RentalRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository properties;
    private final RentalRequestRepository requests;

    // ---------- public search (verified and available properties only) ----------
    public List<Property> search(String location, String type, Integer minRent, Integer maxRent) {
        return properties.findByListingStatusOrderByIdDesc(ListingStatus.VERIFIED).stream()
                .filter(Property::isAvailable)
                .filter(p -> isBlank(location) || contains(p.getLocation(), location) || contains(p.getAddress(), location))
                .filter(p -> isBlank(type) || p.getPropertyType().name().equalsIgnoreCase(type.trim()))
                .filter(p -> minRent == null || p.getRent() >= minRent)
                .filter(p -> maxRent == null || p.getRent() <= maxRent)
                .toList();
    }

    public Property publicDetails(Long id) {
        return properties.findById(id)
                .filter(p -> p.getListingStatus() == ListingStatus.VERIFIED)
                .orElseThrow(() -> new ApiException(404, "Property not found"));
    }

    // ---------- owner ----------
    public List<Property> ofOwner(Long ownerId) {
        return properties.findByOwnerIdOrderByIdDesc(ownerId);
    }

    public Property add(User owner, Property in) {
        Check.that(owner.isVerified(), 403, "Your owner account is waiting for admin verification");
        validate(in);
        Property p = new Property();
        copy(p, in);
        p.setOwner(owner);
        p.setAvailable(true);
        p.setListingStatus(ListingStatus.PENDING);   // admin must verify the listing
        p.setCreatedDate(LocalDate.now());
        return properties.save(p);
    }

    public Property update(User owner, Long id, Property in) {
        Property p = own(owner.getId(), id);
        validate(in);
        copy(p, in);
        return properties.save(p);
    }

    public Property setAvailable(User owner, Long id, boolean available) {
        Property p = own(owner.getId(), id);
        p.setAvailable(available);
        return properties.save(p);
    }

    public void delete(User owner, Long id) {
        own(owner.getId(), id);
        Check.that(!requests.existsByPropertyId(id), 409, "This property has rental requests and cannot be deleted");
        properties.deleteById(id);
    }

    // ---------- admin ----------
    public List<Property> all() {
        return properties.findAllByOrderByIdDesc();
    }

    public Property setListingStatus(Long id, String value) {
        Property p = properties.findById(id).orElseThrow(() -> new ApiException(404, "Property not found"));
        ListingStatus status;
        try {
            status = ListingStatus.valueOf(value.toUpperCase());
        } catch (Exception e) {
            throw new ApiException(400, "Status must be VERIFIED, REJECTED or PENDING");
        }
        p.setListingStatus(status);
        return properties.save(p);
    }

    // ---------- helpers ----------
    private Property own(Long ownerId, Long id) {
        return properties.findById(id)
                .filter(p -> p.getOwner().getId().equals(ownerId))
                .orElseThrow(() -> new ApiException(404, "Property not found"));
    }

    private void validate(Property p) {
        Check.notBlank(p.getTitle(), "Title is required");
        Check.notBlank(p.getLocation(), "City / location is required");
        Check.notBlank(p.getAddress(), "Address is required");
        Check.that(p.getPropertyType() != null, 400, "Choose a property type");
        Check.that(p.getRent() > 0, 400, "Rent must be greater than 0");
        Check.that(p.getBedrooms() >= 0 && p.getBathrooms() >= 0 && p.getAreaSqft() >= 0, 400,
                "Bedrooms, bathrooms and area cannot be negative");
    }

    private void copy(Property to, Property from) {
        to.setTitle(from.getTitle().trim());
        to.setDescription(from.getDescription());
        to.setLocation(from.getLocation().trim());
        to.setAddress(from.getAddress().trim());
        to.setPropertyType(from.getPropertyType());
        to.setRent(from.getRent());
        to.setBedrooms(from.getBedrooms());
        to.setBathrooms(from.getBathrooms());
        to.setAreaSqft(from.getAreaSqft());
        to.setImageUrl(from.getImageUrl());
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private boolean contains(String text, String query) {
        return text != null && text.toLowerCase().contains(query.trim().toLowerCase());
    }
}
