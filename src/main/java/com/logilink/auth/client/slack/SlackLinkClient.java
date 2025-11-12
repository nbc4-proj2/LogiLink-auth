package com.logilink.auth.client.slack;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "slack-service")
public interface SlackLinkClient {

    @PostMapping("/api/slack/link")
    String linkSlack(@RequestBody SlackLinkReq slackLinkReq);
}
