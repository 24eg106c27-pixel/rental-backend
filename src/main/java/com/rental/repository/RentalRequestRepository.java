package com.rental.repository;

import com.rental.model.RentalRequest;
import com.rental.model.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface RentalRequestRepository extends JpaRepository<RentalRequest, Long> {

    List<RentalRequest> findByTenantIdOrderByIdDesc(Long tenantId);

    List<RentalRequest> findByPropertyOwnerIdOrderByIdDesc(Long ownerId);

    List<RentalRequest> findByPropertyIdAndStatus(Long propertyId, RequestStatus status);

    boolean existsByPropertyId(Long propertyId);

    boolean existsByTenantIdAndPropertyIdAndStatusIn(Long tenantId, Long propertyId, Collection<RequestStatus> statuses);

    long countByStatus(RequestStatus status);
}
