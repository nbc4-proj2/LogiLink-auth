package com.logilink.auth.client.delivery;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "delivery-service")
public interface DeliveryLinkClient {

    @PostMapping("/api/v1/delivery-managers")
    void createDeliveryUser(@RequestBody DeliveryUserInfo deliveryUserInfo);
}

// TODO
//  - DeliveryUserInfo 의 필드와 맞추기
//  - 게이트웨이 테스트