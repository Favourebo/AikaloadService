package com.aikaload.repo;

import com.aikaload.entity.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTypeRepo extends JpaRepository<JobType,Long>{
}
