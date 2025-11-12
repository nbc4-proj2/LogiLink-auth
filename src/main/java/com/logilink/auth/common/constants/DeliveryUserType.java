package com.logilink.auth.common.constants;

import lombok.Getter;

@Getter
public enum DeliveryUserType {

    HUB("허브"),
    COMPANY("업체");

    private final String type;

    DeliveryUserType(String type) {
        this.type = type;
    }
}
