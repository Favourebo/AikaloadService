package com.aikaload.repo;

import com.aikaload.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepo extends JpaRepository<PasswordResetToken,Long>{
	public PasswordResetToken findByToken(String token);
}
