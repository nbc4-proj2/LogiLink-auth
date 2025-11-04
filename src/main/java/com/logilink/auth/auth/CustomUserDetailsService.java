package com.logilink.auth.auth;

import com.logilink.auth.model.entity.User;
import com.logilink.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameNotDeleted(username)
            .orElseThrow(() -> new UsernameNotFoundException("username : " + username));

        return new CustomUserDetails(user);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepository.findByIdNotDeleted(userId)
            .orElseThrow(() -> new UsernameNotFoundException("userId : " + userId));

        return new CustomUserDetails(user);
    }
}
