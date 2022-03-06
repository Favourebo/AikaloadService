package com.aikaload.repo;

import com.aikaload.entity.FeeConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeConfigurationRepo extends JpaRepository<FeeConfiguration,Integer>{
}
