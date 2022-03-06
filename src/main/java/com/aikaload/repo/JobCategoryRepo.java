package com.aikaload.repo;

import com.aikaload.entity.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface JobCategoryRepo extends JpaRepository<JobCategory,Long>{
	public Optional<JobCategory> findById(Long id);
}
