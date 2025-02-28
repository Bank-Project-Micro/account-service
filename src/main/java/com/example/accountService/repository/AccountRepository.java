package com.example.accountService.repository;

import com.example.accountService.entity.Account;
import com.example.accountService.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomerId(Long customerId);
    Optional<Account> findByCustomerIdAndType(Long customerId, AccountType type);
}