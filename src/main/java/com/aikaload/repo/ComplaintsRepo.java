package com.aikaload.repo;

import com.aikaload.entity.Complaints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintsRepo extends JpaRepository<Complaints,Long> {

    int countByUserReported(Long id);
}
