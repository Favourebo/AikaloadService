package com.aikaload.repo;

import com.aikaload.entity.DriverInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverInfoRepo extends JpaRepository<DriverInfo,Long>{

	List<DriverInfo> findByIsVerified(int approved);
}
