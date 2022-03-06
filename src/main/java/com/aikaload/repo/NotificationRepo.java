package com.aikaload.repo;

import java.util.List;

import com.aikaload.entity.Notification;
import com.aikaload.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<Notification,Long>{

	public int countByIsReadAndReportFor(boolean isRead, UserAccount userAccount);
	
	public List<Notification> findByReportForOrderByNotificationDateDesc(UserAccount userAccount);

	public List<Notification> findByReportForAndIsRead(UserAccount userAccount, boolean status);

	public Notification findByReportForAndIsReadAndNotificationType(UserAccount updatedUserAccountRsp, boolean b,
			int notificationWarning);
	
}
