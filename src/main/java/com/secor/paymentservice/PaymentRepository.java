package com.secor.paymentservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
  Payment findBySubidIgnoreCase(String subid);

  @Transactional
  @Modifying
  @Query("update Payment p set p.status = ?1 where p.paymentid = ?2")
  int updateStatusByPaymentid(String status, String paymentid);
}