package com.rental.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "properties")
@Getter
@Setter
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // only the owner's id and name are shown publicly
    @ManyToOne(optional = false)
    @JsonIgnoreProperties({"email", "phone", "role", "verified"})
    private User owner;

    private String title;

    @Column(length = 2000)
    private String description;

    private String location;
    private String address;

    @Enumerated(EnumType.STRING)
    private PropertyType propertyType;

    private int rent;
    private int bedrooms;
    private int bathrooms;
    private int areaSqft;

    @Column(length = 1000)
    private String imageUrl;

    private boolean available = true;

    @Enumerated(EnumType.STRING)
    private ListingStatus listingStatus = ListingStatus.PENDING;

    private LocalDate createdDate;
}
