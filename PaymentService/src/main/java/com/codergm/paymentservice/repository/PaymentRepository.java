package com.codergm.paymentservice.repository;

import com.codergm.paymentservice.entity.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<TransactionDetails,Long> {
     Optional<TransactionDetails> findTransactionDetailsByOrderId(Long orderId);
}
