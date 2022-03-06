package com.aikaload.repo;

import com.aikaload.entity.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OauthClientDetailsRepo extends JpaRepository<OauthClientDetails, Long> {

    Optional<OauthClientDetails> findByClientId(String clientId);
}
