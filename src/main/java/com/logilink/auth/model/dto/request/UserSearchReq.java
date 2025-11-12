package com.logilink.auth.model.dto.request;

import org.springframework.data.domain.Sort;

public record UserSearchReq(
    String sortBy,
    String direction,
    Integer page,
    Integer size
) {
    public Integer page() {
        return page ==  null ? 0 : page;
    }

    public Integer size() {
        if (size == null) return 10;

        return switch (size) {
            case 10, 30, 50 -> size;
            default -> 10;
        };
    }

    public String getSortBy() {
        return sortBy == null || sortBy().isBlank() ? "createdAt" : sortBy;
    }

    public Sort.Direction getDirection() {
        return (direction == null || direction.isBlank())
            ? Sort.Direction.DESC : Sort.Direction.valueOf(direction.toUpperCase());
    }
}
