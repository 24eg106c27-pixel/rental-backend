package com.rental.repository;

import com.rental.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByTenantIdOrderByIdDesc(Long tenantId);

    List<Payment> findByRentalRequestPropertyOwnerIdOrderByIdDesc(Long ownerId);

    List<Payment> findAllByOrderByIdDesc();

    @Query("select coalesce(sum(p.amount), 0L) from Payment p")
    long totalAmount();
}
