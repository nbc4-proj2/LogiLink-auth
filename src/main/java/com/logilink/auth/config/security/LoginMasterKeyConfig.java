package com.logilink.auth.config.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class LoginMasterKeyConfig {

    @Value("${login.master.key}")
    private String masterSecretKey;
}
