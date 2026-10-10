package com.rental.repository;

import com.rental.model.Complaint;
import com.rental.model.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByRaisedByIdOrderByIdDesc(Long userId);

    List<Complaint> findAllByOrderByIdDesc();

    long countByStatus(ComplaintStatus status);
}
