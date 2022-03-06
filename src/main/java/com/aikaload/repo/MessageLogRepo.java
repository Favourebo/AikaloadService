package com.aikaload.repo;

import com.aikaload.entity.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageLogRepo extends JpaRepository<MessageLog,Long> {
    List<MessageLog> findByRecipientId(Long userId);
}
