package com.example.accountService.service;

import com.example.accountService.dto.AccountDTO;
import com.example.accountService.entity.Account;
import com.example.accountService.repository.AccountRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate;
    private final String customerServiceUrl = "http://customer-service/api/customers/";

    public AccountService(AccountRepository accountRepository, RestTemplate restTemplate) {
        this.accountRepository = accountRepository;
        this.restTemplate = restTemplate;
    }

    public AccountDTO createAccount(AccountDTO accountDTO) {

        Map<String, Object> customer = restTemplate.getForObject(
                customerServiceUrl + accountDTO.getCustomerId(),
                Map.class
        );

        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }


        if (accountRepository.findByCustomerIdAndType(
                        accountDTO.getCustomerId(),
                        accountDTO.getType())
                .isPresent()) {
            throw new RuntimeException("Customer already has this type of account");
        }

        Account account = new Account();
        BeanUtils.copyProperties(accountDTO, account);
        account = accountRepository.save(account);

        AccountDTO savedDTO = new AccountDTO();
        BeanUtils.copyProperties(account, savedDTO);
        savedDTO.setCustomerName((String) customer.get("name"));

        return savedDTO;
    }

    public List<AccountDTO> getCustomerAccounts(Long customerId) {
        Map<String, Object> customer = restTemplate.getForObject(
                customerServiceUrl + customerId,
                Map.class
        );

        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        return accountRepository.findByCustomerId(customerId).stream()
                .map(account -> {
                    AccountDTO dto = new AccountDTO();
                    BeanUtils.copyProperties(account, dto);
                    dto.setCustomerName((String) customer.get("name"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public AccountDTO getAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Map<String, Object> customer = restTemplate.getForObject(
                customerServiceUrl + account.getCustomerId(),
                Map.class
        );

        AccountDTO accountDTO = new AccountDTO();
        BeanUtils.copyProperties(account, accountDTO);
        if (customer != null) {
            accountDTO.setCustomerName((String) customer.get("name"));
        }

        return accountDTO;
    }
}
