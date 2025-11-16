package com.bidgrove.authservice.repository;

import com.bidgrove.authservice.model.AccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccessTokenRepository extends JpaRepository<AccessToken, Long> {

    Optional<AccessToken> findByToken(String token);

    @Query("SELECT t FROM AccessToken t WHERE t.user.email = :email")
    List<AccessToken> findAllByUserEmail(String email);
}
