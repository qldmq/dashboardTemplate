package com.dashboardTemplate.dashboardTemplate.config;

import com.dashboardTemplate.dashboardTemplate.domain.auth.entity.Auth;
import com.dashboardTemplate.dashboardTemplate.domain.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepository authRepository;

    @Override
    public UserDetails loadUserByUsername(String companyId) throws UsernameNotFoundException {
        Auth auth = authRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with companyId: " + companyId));
        return new UserDetailsImpl(auth);
    }
}
