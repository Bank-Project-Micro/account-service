package com.example.accountService.dto;


import com.example.accountService.enums.AccountType;
import lombok.Data;

@Data
public class AccountDTO {
    private Long id;
    private Double balance;
    private AccountType type;
    private Long customerId;
    private String customerName;
}