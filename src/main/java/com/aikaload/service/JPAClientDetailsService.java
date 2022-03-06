package com.aikaload.service;


import com.aikaload.entity.OauthClientDetails;
import com.aikaload.repo.OauthClientDetailsRepo;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
@AllArgsConstructor
@Log4j2
public class  JPAClientDetailsService implements ClientDetailsService {

    private OauthClientDetailsRepo oauthClientDetailsRepo;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

        Optional<OauthClientDetails> oauthClientDetails = oauthClientDetailsRepo.findByClientId(clientId);

        if(oauthClientDetails.isPresent()){
            return  new ClientDetails() {
                @Override
                public String getClientId() {
                    return clientId;
                }

                @Override
                public Set<String> getResourceIds() {
                    return new HashSet<>(Arrays.asList(oauthClientDetails.get().getResourceIds().split(",")));
                }

                @Override
                public boolean isSecretRequired() {
                    return true;
                }

                @Override
                public String getClientSecret() {
                    return oauthClientDetails.get().getClientSecret();
                }

                @Override
                public boolean isScoped() {
                    return true;
                }

                @Override
                public Set<String> getScope() {
                    return new HashSet<>(Arrays.asList(oauthClientDetails.get().getScope().split(",")));
                }

                @Override
                public Set<String> getAuthorizedGrantTypes() {
                    return  new HashSet<>(Arrays.asList(oauthClientDetails.get().getAuthorizedGrantTypes().split(",")));
                }

                @Override
                public Set<String> getRegisteredRedirectUri() {
                    return null;
                }

                @Override
                public Collection<GrantedAuthority> getAuthorities() {
                    Collection<GrantedAuthority>  grantedAuthorities=  new ArrayList<>();
                    for (String grantedAuthorityTypes : oauthClientDetails.get().getAuthorities().split(",")){
                        grantedAuthorities.add(new SimpleGrantedAuthority(grantedAuthorityTypes));
                     }
                     return grantedAuthorities;
                }

                @Override
                public Integer getAccessTokenValiditySeconds() {
                    return oauthClientDetails.get().getAccessTokenValidity();
                }

                @Override
                public Integer getRefreshTokenValiditySeconds() {
                    return oauthClientDetails.get().getRefreshTokenValidity();
                }

                @Override
                public boolean isAutoApprove(String scope) {
                    return oauthClientDetails.get().isAutoApprove();
                }

                @Override
                public Map<String, Object> getAdditionalInformation() {
                    return null;
                }
            };
           }
        log.info("<<<<<<Oauth Client Details Retrieval Failed for::"+clientId);
        return null;
        }



}
