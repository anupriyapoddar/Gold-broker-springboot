package org.example.goldbroker.controller;

import org.example.goldbroker.dto.PriceDto;
import org.example.goldbroker.service.PriceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// src/main/java/org/example/goldbroker/controller/PriceController.java

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/latest")
    public PriceDto latest(@RequestParam("metal") String metal) { // <-- changed
        return priceService.fetchLatestPrice(metal);
    }
}
