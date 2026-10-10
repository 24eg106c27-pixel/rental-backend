package com.rental.controller;

import com.rental.model.Property;
import com.rental.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// no login needed: anyone can browse verified, available properties
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final PropertyService properties;

    @GetMapping("/properties")
    public List<Property> search(@RequestParam(required = false) String location,
                                 @RequestParam(required = false) String type,
                                 @RequestParam(required = false) Integer minRent,
                                 @RequestParam(required = false) Integer maxRent) {
        return properties.search(location, type, minRent, maxRent);
    }

    @GetMapping("/properties/{id}")
    public Property details(@PathVariable Long id) {
        return properties.publicDetails(id);
    }
}
