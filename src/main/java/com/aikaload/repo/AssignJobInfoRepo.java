package com.aikaload.repo;

import com.aikaload.entity.AssignJobInfo;
import com.aikaload.entity.JobInfo;
import com.aikaload.entity.UserAccount;
import com.aikaload.enums.JobEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignJobInfoRepo extends JpaRepository<AssignJobInfo,Long>{

	AssignJobInfo findByAssignedToAndJobInfo(UserAccount userAccount, JobInfo jobInfo);

    List<AssignJobInfo> findByAssignedTo(UserAccount userAccount);

    List<AssignJobInfo> findByJobInfo(JobInfo jobInfo);

    List<AssignJobInfo> findByJobInfoAndIsAssignedAndIsTaskClosed(JobInfo jobInfo, boolean isAssigned,boolean isTaskClosed);

    int countByJobInfoAndIsAssignedAndIsTaskClosed(JobInfo jobInfo, boolean isAssigned,boolean isTaskClosed);

    AssignJobInfo findByJobInfoAndAssignedToAndIsAssignedAndIsTaskClosed(JobInfo jobInfo, UserAccount userAccount, boolean isAssigned, boolean isTaskClosed);

    int countByAssignedToAndIsTaskClosed(UserAccount userAccount, boolean isTaskClosed);

    AssignJobInfo findByToken(String token);

    List<AssignJobInfo> findByJobInfoAndIsTaskClosed(JobInfo jobInfo, boolean status);

    List<AssignJobInfo> findByAssignedToAndIsAssignedAndIsTaskClosed(UserAccount userAccount, boolean isAssigned, boolean isTaskClosed);

    int countByJobInfo(JobInfo jobInfo);
}
