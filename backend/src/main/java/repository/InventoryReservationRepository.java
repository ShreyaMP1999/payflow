package com.payflow.repository;

import com.payflow.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {
  List<InventoryReservation> findByStatusAndExpiresAtBefore(InventoryReservation.Status status, Instant time);
}
