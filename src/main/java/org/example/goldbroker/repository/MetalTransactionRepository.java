package org.example.goldbroker.repository;

import org.example.goldbroker.model.MetalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetalTransactionRepository extends JpaRepository<MetalTransaction, Long> {
}
