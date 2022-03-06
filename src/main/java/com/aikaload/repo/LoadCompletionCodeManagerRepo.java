package com.aikaload.repo;

import com.aikaload.entity.LoadCompletionCodeManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoadCompletionCodeManagerRepo extends JpaRepository<LoadCompletionCodeManager,Long> {
    List<LoadCompletionCodeManager> findByAssignJobId(Long id);

    LoadCompletionCodeManager findByCompletionCode(String completionCode);

    LoadCompletionCodeManager findByLoadCode(String loadCode);

    LoadCompletionCodeManager findByTruckId(int truckId);

    LoadCompletionCodeManager findByTruckIdAndAssignJobId(int truckId, Long id);
}
