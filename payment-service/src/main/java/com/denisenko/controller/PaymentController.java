package com.denisenko.controller;

import com.denisenko.dto.AccountDto;
import com.denisenko.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment/accounts")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(paymentService.createAccount(accountDto));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getAccount(userId));
    }
}
