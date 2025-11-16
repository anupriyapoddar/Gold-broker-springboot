package org.example.goldbroker.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "metal_holding")
public class MetalHolding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String metal;

    @Column(nullable = false)
    private BigDecimal quantity; // in grams

    @Column(nullable = false)
    private BigDecimal avgPricePerUnit; // INR per gram (cost basis)

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    public MetalHolding() {
    }

    public MetalHolding(String metal,
            BigDecimal quantity,
            BigDecimal avgPricePerUnit,
            AppUser user) {
        this.metal = metal;
        this.quantity = quantity;
        this.avgPricePerUnit = avgPricePerUnit;
        this.user = user;
    }

    // ---- GETTERS ----
    public Long getId() {
        return id;
    }

    public String getMetal() {
        return metal;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public BigDecimal getAvgPricePerUnit() {
        return avgPricePerUnit;
    }

    public AppUser getUser() {
        return user;
    }

    // ---- SETTERS ----
    public void setMetal(String metal) {
        this.metal = metal;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public void setAvgPricePerUnit(BigDecimal avgPricePerUnit) {
        this.avgPricePerUnit = avgPricePerUnit;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }
}
