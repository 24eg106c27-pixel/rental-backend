package com.rental.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private RentalRequest rentalRequest;

    @ManyToOne(optional = false)
    private User tenant;

    private int amount;
    private LocalDate paymentDate;
    private String method;
    private String reference;
}
