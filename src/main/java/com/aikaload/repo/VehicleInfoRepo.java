package com.aikaload.repo;

import java.util.List;

import com.aikaload.entity.VehicleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehicleInfoRepo extends JpaRepository<VehicleInfo,Long>{

	List<VehicleInfo> findByIsVerified(int approved);
}
