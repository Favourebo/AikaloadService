package com.aikaload.service;

import com.aikaload.dto.SpringSecurityUserDetails;
import com.aikaload.entity.*;
import com.aikaload.repo.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
@AllArgsConstructor
@Log4j2
public class JPAUserDetailsService implements UserDetailsService {
    private UserAccountRepo userAccountRepo;


    @Override
    public SpringSecurityUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        UserAccount userAccount = userAccountRepo.findByUsernameAndIsAccountVerified(username.toLowerCase().trim(), true);
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + userAccount.getUserRole().getUserRoleName()));
        return new SpringSecurityUserDetails(userAccount.getUsername(),
                userAccount.getPassword(), true, true, true, true, grantedAuthorities, userAccount.getId(),
                userAccount.getCompanyName(), userAccount.getContactPersonName(), userAccount.isAccountVerified(),
                userAccount.isAccountInformationUpdated());
    }

}
