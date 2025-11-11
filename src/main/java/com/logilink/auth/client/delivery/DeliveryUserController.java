package com.logilink.auth.client.delivery;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users/delivery-user")
@RequiredArgsConstructor
public class DeliveryUserController {

    private final DeliveryUserService deliveryUserService;

    @GetMapping("/{userId}")
    public ResponseEntity<DeliveryUserInfo> getDeliveryUser(@PathVariable Long userId) {
       return ResponseEntity.ok(deliveryUserService.getDeliveryUserInfo(userId));
    }
}
