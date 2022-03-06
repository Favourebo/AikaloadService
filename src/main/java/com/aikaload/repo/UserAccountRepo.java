package com.aikaload.repo;

import java.util.List;
import com.aikaload.entity.UserAccount;
import com.aikaload.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepo extends JpaRepository<UserAccount,Long>{
	public UserAccount findByUsernameAndIsAccountVerified(String username, boolean isAccountVerified);
	
	public UserAccount findByUsername(String username);
	
	@Query("select u from UserAccount u inner join u.userRole ur where ur.id = :user_role_id")
	public List<UserAccount> findByUserRoleId(@Param("user_role_id") Long user_role_id);

	@Query("select u from UserAccount u inner join u.userRole ur where ur.userRoleName = :userRoleName")
	public List<UserAccount> findByUserRoleName(@Param("userRoleName") String userRoleName);

	public List<UserAccount> findByUserRole(UserRole userRole);

	public UserAccount findByCompanyName(String companyName);

	public UserAccount findByMobilePhone(String mobilePhone);
}
