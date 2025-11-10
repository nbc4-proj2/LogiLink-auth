package com.logilink.auth.model.dto.request;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public record UserSearchReq(
    String sortBy,
    Sort.Direction direction,
    int page,
    int size
) {
    public int getSize() {
        return switch (size) {
            case 10, 30, 50 -> size;
            default -> 10;
        };
    }

    public String getSortBy() {
        return sortBy == null || sortBy().isBlank() ? "createdAt" : sortBy;
    }

    public Sort.Direction getDirection() {
        return direction == null ?  Direction.DESC : direction;
    }
}
