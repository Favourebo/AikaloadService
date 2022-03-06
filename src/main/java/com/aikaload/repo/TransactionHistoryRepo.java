package com.aikaload.repo;

import com.aikaload.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepo extends JpaRepository<TransactionHistory,Long> {
    List<TransactionHistory> findByUserId(long userId);
}
