package com.aikaload.repo;

import com.aikaload.entity.PackageSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageSpaceRepo extends JpaRepository<PackageSpace,Long>{

}
