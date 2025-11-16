package org.example.goldbroker.repository;

import org.example.goldbroker.model.MetalHolding;
import org.example.goldbroker.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MetalHoldingRepository extends JpaRepository<MetalHolding, Long> {

    Optional<MetalHolding> findByUserAndMetalIgnoreCase(AppUser user, String metal);

    List<MetalHolding> findAllByUser(AppUser user);
}
