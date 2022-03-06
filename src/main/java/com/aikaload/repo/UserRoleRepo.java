package com.aikaload.repo;


import com.aikaload.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserRoleRepo extends JpaRepository<UserRole, Long>{

    UserRole findByUserRoleName(String truckOwner);
}
