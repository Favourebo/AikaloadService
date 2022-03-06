package com.aikaload.repo;

import com.aikaload.entity.LoadLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadLevelRepo extends JpaRepository<LoadLevel,Integer>{
}
