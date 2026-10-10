package com.rental.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "rental_requests")
@Getter
@Setter
public class RentalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Property property;

    @ManyToOne(optional = false)
    private User tenant;

    @Column(length = 1000)
    private String message;

    private LocalDate moveInDate;
    private int months;
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDate requestDate;
    private LocalDate decisionDate;
}
