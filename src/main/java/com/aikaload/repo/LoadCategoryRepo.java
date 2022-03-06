package com.aikaload.repo;

import com.aikaload.entity.LoadCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface LoadCategoryRepo extends JpaRepository<LoadCategory,Long>{
}
