
package com.smartexam.auth.security;

import com.smartexam.user.entity.User;
import com.smartexam.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email
                        )
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(
                        user.getRoles().stream()
                                .map(role ->
                                        new SimpleGrantedAuthority(
                                                role.getName()
                                        )
                                )
                                .collect(Collectors.toSet())
                )
                .accountExpired(false)
                .accountLocked(!Boolean.TRUE.equals(user.getAccountNonLocked()))
                .credentialsExpired(false)
                .disabled(!Boolean.TRUE.equals(user.getEnabled()))
                .build();
    }
}
