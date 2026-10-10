package com.rental.config;

import com.rental.model.*;
import com.rental.repository.PropertyRepository;
import com.rental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// adds demo users and sample properties the first time the app starts
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository users;
    private final PropertyRepository properties;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (users.count() > 0) {
            return;
        }
        users.save(user("Administrator", "admin@rental.com", "admin123", "9000000001", Role.ADMIN));
        User owner = users.save(user("Ravi Kumar", "owner@rental.com", "owner123", "9000000002", Role.OWNER));
        users.save(user("Anjali Rao", "tenant@rental.com", "tenant123", "9000000003", Role.TENANT));

        properties.save(property(owner, "Spacious 2BHK near Hitech City", "Hyderabad", "Madhapur, Hyderabad",
                PropertyType.APARTMENT, 24000, 2, 2, 1150, "house1",
                "Well ventilated 2BHK with covered parking, 24x7 water and security. Five minutes from the metro."));
        properties.save(property(owner, "Independent House with Garden", "Bengaluru", "Indiranagar, Bengaluru",
                PropertyType.HOUSE, 42000, 3, 3, 1800, "house2",
                "Quiet independent house with a small garden, close to schools and restaurants."));
        properties.save(property(owner, "Sea Facing Luxury Villa", "Chennai", "ECR, Chennai",
                PropertyType.VILLA, 85000, 4, 4, 3200, "house3",
                "Four bedroom villa with a private terrace and a view of the sea. Fully furnished."));
        properties.save(property(owner, "Furnished PG for Working Professionals", "Pune", "Hinjewadi, Pune",
                PropertyType.PG, 9500, 1, 1, 250, "house4",
                "Single sharing room with meals, Wi-Fi and laundry included."));
        properties.save(property(owner, "Studio Apartment in the City Centre", "Mumbai", "Andheri West, Mumbai",
                PropertyType.APARTMENT, 31000, 1, 1, 520, "house5",
                "Compact studio with modular kitchen, ideal for a single person or a couple."));
        properties.save(property(owner, "Ground Floor Shop for Rent", "Hyderabad", "Kukatpally, Hyderabad",
                PropertyType.COMMERCIAL, 38000, 0, 1, 700, "house6",
                "Main road facing shop with good footfall and a separate store room."));
    }

    private User user(String name, String email, String password, String phone, Role role) {
        User u = new User();
        u.setName(name);
        u.setEmail(email);
        u.setPassword(encoder.encode(password));
        u.setPhone(phone);
        u.setRole(role);
        u.setVerified(true);
        return u;
    }

    private Property property(User owner, String title, String location, String address, PropertyType type,
                              int rent, int beds, int baths, int area, String imageSeed, String description) {
        Property p = new Property();
        p.setOwner(owner);
        p.setTitle(title);
        p.setLocation(location);
        p.setAddress(address);
        p.setPropertyType(type);
        p.setRent(rent);
        p.setBedrooms(beds);
        p.setBathrooms(baths);
        p.setAreaSqft(area);
        p.setImageUrl("https://picsum.photos/seed/" + imageSeed + "/640/420");
        p.setDescription(description);
        p.setAvailable(true);
        p.setListingStatus(ListingStatus.VERIFIED);
        p.setCreatedDate(LocalDate.now());
        return p;
    }
}
