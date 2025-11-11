package com.logilink.auth.client.slack;

import static com.logilink.auth.common.exception.UserErrorCode.*;

import com.logilink.auth.common.exception.AppException;
import com.logilink.auth.model.entity.User;
import com.logilink.auth.repository.UserRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SlackLinkService {

    private final SlackLinkClient slackClient;
    private final UserRepository userRepository;

    public SlackLinkRes linkSlack(Long userId) {
        // 유저 조회
        User user = userRepository.findById(userId)
            .orElseThrow(() -> AppException.of(USER_NOT_FOUND));

        // slack api 요정 dto
        SlackLinkReq slackLinkReq = SlackLinkReq.from(user);

        SlackLinkRes slackLinkRes;

        // feign client 호출
        try {
            slackLinkRes = slackClient.linkSlack(slackLinkReq);
        } catch (FeignException e) {
            throw AppException.of(SLACK_SERVICE_ERROR);
        }

        // 더티체킹으로 db 업데이트
        user.updateSlackId(slackLinkRes.slackUserId());

        return slackLinkRes;
    }
}
