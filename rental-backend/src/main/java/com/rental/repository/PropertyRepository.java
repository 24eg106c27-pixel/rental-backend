package com.rental.repository;

import com.rental.model.ListingStatus;
import com.rental.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    List<Property> findByOwnerIdOrderByIdDesc(Long ownerId);

    List<Property> findByListingStatusOrderByIdDesc(ListingStatus status);

    List<Property> findAllByOrderByIdDesc();

    long countByListingStatus(ListingStatus status);

    long countByAvailableAndListingStatus(boolean available, ListingStatus status);
}
