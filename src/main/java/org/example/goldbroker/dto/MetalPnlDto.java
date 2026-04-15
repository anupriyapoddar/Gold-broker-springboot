package org.example.goldbroker.dto;

import java.math.BigDecimal;

public class MetalPnlDto {
    private String metal;
    private BigDecimal quantity; // grams
    private BigDecimal avgPricePerUnit; // INR/gram (cost)
    private BigDecimal currentPricePerUnit; // INR/gram (market)
    private BigDecimal investedValue; // INR
    private BigDecimal currentValue; // INR
    private BigDecimal pnlAmount; // INR
    private BigDecimal pnlPercent; // %

    public String getMetal() {
        return metal;
    }

    public void setMetal(String metal) {
        this.metal = metal;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAvgPricePerUnit() {
        return avgPricePerUnit;
    }

    public void setAvgPricePerUnit(BigDecimal avgPricePerUnit) {
        this.avgPricePerUnit = avgPricePerUnit;
    }

    public BigDecimal getCurrentPricePerUnit() {
        return currentPricePerUnit;
    }

    public void setCurrentPricePerUnit(BigDecimal currentPricePerUnit) {
        this.currentPricePerUnit = currentPricePerUnit;
    }

    public BigDecimal getInvestedValue() {
        return investedValue;
    }

    public void setInvestedValue(BigDecimal investedValue) {
        this.investedValue = investedValue;
    }

    public BigDecimal getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(BigDecimal currentValue) {
        this.currentValue = currentValue;
    }

    public BigDecimal getPnlAmount() {
        return pnlAmount;
    }

    public void setPnlAmount(BigDecimal pnlAmount) {
        this.pnlAmount = pnlAmount;
    }

    public BigDecimal getPnlPercent() {
        return pnlPercent;
    }

    public void setPnlPercent(BigDecimal pnlPercent) {
        this.pnlPercent = pnlPercent;
    }
}
