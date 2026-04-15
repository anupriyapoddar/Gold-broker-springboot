package org.example.goldbroker.controller;

import org.example.goldbroker.dto.PriceDto;
import org.example.goldbroker.service.PriceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/latest")
    public ResponseEntity<?> latest(@RequestParam("metal") String metal) {
        try {
            PriceDto dto = priceService.fetchLatestPrice(metal);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace(); // logs real error in console
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Price fetch failed: " + e.getMessage());
        }
    }
}
