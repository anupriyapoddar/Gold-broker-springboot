package org.example.goldbroker.dto;

import java.math.BigDecimal;
import java.util.List;

public class PortfolioSummaryDto {

    private BigDecimal totalInvested; // sum of investedValue
    private BigDecimal totalCurrentValue; // sum of currentValue
    private BigDecimal totalPnlAmount; // current - invested
    private BigDecimal totalPnlPercent; // %

    private List<MetalPnlDto> metals;

    public BigDecimal getTotalInvested() {
        return totalInvested;
    }

    public void setTotalInvested(BigDecimal totalInvested) {
        this.totalInvested = totalInvested;
    }

    public BigDecimal getTotalCurrentValue() {
        return totalCurrentValue;
    }

    public void setTotalCurrentValue(BigDecimal totalCurrentValue) {
        this.totalCurrentValue = totalCurrentValue;
    }

    public BigDecimal getTotalPnlAmount() {
        return totalPnlAmount;
    }

    public void setTotalPnlAmount(BigDecimal totalPnlAmount) {
        this.totalPnlAmount = totalPnlAmount;
    }

    public BigDecimal getTotalPnlPercent() {
        return totalPnlPercent;
    }

    public void setTotalPnlPercent(BigDecimal totalPnlPercent) {
        this.totalPnlPercent = totalPnlPercent;
    }

    public List<MetalPnlDto> getMetals() {
        return metals;
    }

    public void setMetals(List<MetalPnlDto> metals) {
        this.metals = metals;
    }
}
