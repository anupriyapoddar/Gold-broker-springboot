package org.example.goldbroker.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class PriceDto {
    private String metal;
    private BigDecimal pricePerUnit;
    private Instant timestamp;

    public PriceDto() {
    }

    public PriceDto(String metal, BigDecimal pricePerUnit, Instant timestamp) {
        this.metal = metal;
        this.pricePerUnit = pricePerUnit;
        this.timestamp = timestamp;
    }

    public String getMetal() {
        return metal;
    }

    public void setMetal(String metal) {
        this.metal = metal;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
