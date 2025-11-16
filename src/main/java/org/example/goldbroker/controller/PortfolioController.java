package org.example.goldbroker.controller;

import org.example.goldbroker.dto.PortfolioSummaryDto;
import org.example.goldbroker.model.MetalHolding;
import org.example.goldbroker.model.MetalTransaction;
import org.example.goldbroker.service.PortfolioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

// src/main/java/org/example/goldbroker/controller/PortfolioController.java

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping("/holdings")
    public List<MetalHolding> getHoldings() {
        return portfolioService.getHoldings();
    }

    @GetMapping("/summary")
    public PortfolioSummaryDto getSummary() {
        return portfolioService.getPortfolioSummary();
    }

    @PostMapping("/buy")
    public MetalTransaction buy(@RequestParam("metal") String metal, // <-- add "metal"
            @RequestParam("quantity") BigDecimal quantity) { // <-- add "quantity"
        return portfolioService.buy(metal, quantity);
    }

    @PostMapping("/sell")
    public MetalTransaction sell(@RequestParam("metal") String metal, // <-- add "metal"
            @RequestParam("quantity") BigDecimal quantity) { // <-- add "quantity"
        return portfolioService.sell(metal, quantity);
    }
}
