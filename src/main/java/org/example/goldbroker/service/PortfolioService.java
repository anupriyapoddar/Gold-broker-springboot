package org.example.goldbroker.service;

import org.example.goldbroker.dto.MetalPnlDto;
import org.example.goldbroker.dto.PortfolioSummaryDto;

import java.util.ArrayList;
import java.math.RoundingMode;
import org.example.goldbroker.dto.PriceDto;
import org.example.goldbroker.model.AppUser;
import org.example.goldbroker.model.MetalHolding;
import org.example.goldbroker.model.MetalTransaction;
import org.example.goldbroker.repository.AppUserRepository;
import org.example.goldbroker.repository.MetalHoldingRepository;
import org.example.goldbroker.repository.MetalTransactionRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PortfolioService {

    private final MetalHoldingRepository holdingRepository;
    private final MetalTransactionRepository transactionRepository;
    private final PriceService priceService;
    private final AppUserRepository userRepository;

    public PortfolioService(MetalHoldingRepository holdingRepository,
            MetalTransactionRepository transactionRepository,
            PriceService priceService,
            AppUserRepository userRepository) {
        this.holdingRepository = holdingRepository;
        this.transactionRepository = transactionRepository;
        this.priceService = priceService;
        this.userRepository = userRepository;
    }

    public PortfolioSummaryDto getPortfolioSummary() {
        AppUser user = getCurrentUser();
        List<MetalHolding> holdings = holdingRepository.findAllByUser(user);

        PortfolioSummaryDto summary = new PortfolioSummaryDto();
        List<MetalPnlDto> metalSummaries = new ArrayList<>();

        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalCurrent = BigDecimal.ZERO;

        for (MetalHolding holding : holdings) {
            String metal = holding.getMetal();
            BigDecimal qty = holding.getQuantity(); // grams
            BigDecimal avgPrice = holding.getAvgPricePerUnit(); // INR/gram

            if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            // Get live price in INR/gram
            PriceDto priceDto = priceService.fetchLatestPrice(metal);
            BigDecimal currentPrice = priceDto.getPricePerUnit();

            BigDecimal investedValue = avgPrice.multiply(qty);
            BigDecimal currentValue = currentPrice.multiply(qty);

            BigDecimal pnlAmount = currentValue.subtract(investedValue);
            BigDecimal pnlPercent = investedValue.compareTo(BigDecimal.ZERO) > 0
                    ? pnlAmount.multiply(BigDecimal.valueOf(100))
                            .divide(investedValue, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            totalInvested = totalInvested.add(investedValue);
            totalCurrent = totalCurrent.add(currentValue);

            MetalPnlDto metalPnl = new MetalPnlDto();
            metalPnl.setMetal(metal);
            metalPnl.setQuantity(qty);
            metalPnl.setAvgPricePerUnit(avgPrice);
            metalPnl.setCurrentPricePerUnit(currentPrice);
            metalPnl.setInvestedValue(investedValue);
            metalPnl.setCurrentValue(currentValue);
            metalPnl.setPnlAmount(pnlAmount);
            metalPnl.setPnlPercent(pnlPercent);

            metalSummaries.add(metalPnl);
        }

        BigDecimal totalPnl = totalCurrent.subtract(totalInvested);
        BigDecimal totalPnlPercent = totalInvested.compareTo(BigDecimal.ZERO) > 0
                ? totalPnl.multiply(BigDecimal.valueOf(100))
                        .divide(totalInvested, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        summary.setTotalInvested(totalInvested);
        summary.setTotalCurrentValue(totalCurrent);
        summary.setTotalPnlAmount(totalPnl);
        summary.setTotalPnlPercent(totalPnlPercent);
        summary.setMetals(metalSummaries);

        return summary;
    }

    /**
     * Helper: get the currently authenticated AppUser from SecurityContext.
     */
    private AppUser getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName(); // subject we put in JWT

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Current user not found: " + email));
    }

    /**
     * List holdings for the currently logged-in user.
     */
    public List<MetalHolding> getHoldings() {
        AppUser user = getCurrentUser();
        return holdingRepository.findAllByUser(user);
    }

    /**
     * Buy metal (quantity in grams) for the current user.
     * Uses live INR/gram price from PriceService and updates weighted average cost.
     */
    @Transactional
    public MetalTransaction buy(String metal, BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        AppUser user = getCurrentUser();

        // 1) Fetch current INR/gram price
        PriceDto priceDto = priceService.fetchLatestPrice(metal);
        BigDecimal unitPrice = priceDto.getPricePerUnit(); // INR per gram

        // 2) Save transaction (global for now – not user-specific)
        MetalTransaction tx = new MetalTransaction(
                metal.toUpperCase(),
                unitPrice,
                quantity,
                "BUY");
        transactionRepository.save(tx);

        // 3) Update holdings for this user + metal
        MetalHolding holding = holdingRepository
                .findByUserAndMetalIgnoreCase(user, metal)
                .orElseGet(() -> {
                    MetalHolding h = new MetalHolding();
                    h.setUser(user);
                    h.setMetal(metal.toUpperCase());
                    h.setQuantity(BigDecimal.ZERO);
                    h.setAvgPricePerUnit(BigDecimal.ZERO);
                    return h;
                });

        BigDecimal oldQty = holding.getQuantity();
        BigDecimal newQty = oldQty.add(quantity);

        BigDecimal oldTotalCost = holding.getAvgPricePerUnit().multiply(oldQty);
        BigDecimal newCost = unitPrice.multiply(quantity);
        BigDecimal newTotalCost = oldTotalCost.add(newCost);

        BigDecimal newAvgPrice = newQty.compareTo(BigDecimal.ZERO) > 0
                ? newTotalCost.divide(newQty, 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        holding.setQuantity(newQty);
        holding.setAvgPricePerUnit(newAvgPrice);
        holding.setUser(user);

        holdingRepository.save(holding);

        return tx;
    }

    /**
     * Sell metal (quantity in grams) for the current user.
     * Uses live INR/gram price, decreases quantity; does NOT change avg cost basis.
     */
    @Transactional
    public MetalTransaction sell(String metal, BigDecimal quantity) {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        AppUser user = getCurrentUser();

        // 1) Find holding for this user + metal
        MetalHolding holding = holdingRepository
                .findByUserAndMetalIgnoreCase(user, metal)
                .orElseThrow(() -> new IllegalArgumentException("No holdings for metal: " + metal));

        if (holding.getQuantity().compareTo(quantity) < 0) {
            throw new IllegalArgumentException("Insufficient quantity to sell");
        }

        // 2) Fetch current price
        PriceDto priceDto = priceService.fetchLatestPrice(metal);
        BigDecimal unitPrice = priceDto.getPricePerUnit();

        // 3) Save transaction (negative quantity for sell)
        MetalTransaction tx = new MetalTransaction(
                metal.toUpperCase(),
                unitPrice,
                quantity.negate(),
                "SELL");
        transactionRepository.save(tx);

        // 4) Update holding quantity
        holding.setQuantity(holding.getQuantity().subtract(quantity));
        holdingRepository.save(holding);

        return tx;
    }
}
