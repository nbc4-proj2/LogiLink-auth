package com.logilink.auth.client.slack;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class SlackLinkController {

    private final SlackLinkService slackLinkService;

    @PostMapping("/{userId}/slack/link")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<SlackLinkRes> linkSlack(@PathVariable Long userId) {
        SlackLinkRes slackLinkRes = slackLinkService.linkSlack(userId);
        return ResponseEntity.ok(slackLinkRes);
    }
}
