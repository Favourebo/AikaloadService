package com.aikaload.repo;

import com.aikaload.entity.TruckModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TruckModelRepo extends JpaRepository<TruckModel, Integer> {
}
