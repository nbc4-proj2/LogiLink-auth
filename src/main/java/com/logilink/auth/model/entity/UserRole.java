package com.logilink.auth.model.entity;

public enum UserRole {

    MASTER,
    HUB_MANAGER,
    HUB_DELIVERY_MANAGER,
    COMPANY_DELIVERY_MANAGER,
    COMPANY_MANAGER;

    /**
     * 배송 담당자 여부 확인
     */
    public boolean isDeliveryRole() {
        return this == HUB_DELIVERY_MANAGER || this == COMPANY_DELIVERY_MANAGER;
    }

    /**
     * 허브 관련 역할 확인
     */
    public boolean isHubRole() {
        return this == HUB_MANAGER || this == HUB_DELIVERY_MANAGER;
    }

    /**
     * 업체 관련 역할 확인
     */
    public boolean isCompanyRole() {
        return this == COMPANY_MANAGER || this == COMPANY_DELIVERY_MANAGER;
    }
}
