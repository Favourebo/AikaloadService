package com.aikaload.repo;

import com.aikaload.entity.TruckType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckTypeRepo extends JpaRepository<TruckType,Integer>{
}
