package com.logilink.auth.model.dto.response;

import com.logilink.auth.model.entity.User;;
import java.util.List;
import org.springframework.data.domain.Page;

public record UserPageRes(
   List<UserSearchRes> contents,
   int page,
   int size,
   long totalElements,
   int totalPages
) {
    public static UserPageRes of(Page<User> userPage) {
        List<UserSearchRes> contents = userPage.stream().map(UserSearchRes::from).toList();

        return new UserPageRes(
            contents,
            userPage.getNumber(),
            userPage.getSize(),
            userPage.getTotalElements(),
            userPage.getTotalPages()
        );
    }
}
