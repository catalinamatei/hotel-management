package com.hotel_management.booking_api.repository;

import com.hotel_management.booking_api.entity.CustomerEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
  Optional<CustomerEntity> findByEmail(String email);
}
