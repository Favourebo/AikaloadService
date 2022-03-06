package com.aikaload.repo;

import com.aikaload.entity.JobInfo;
import com.aikaload.entity.UserAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobInfoRepo extends PagingAndSortingRepository<JobInfo,Long> {

	public List<JobInfo> findTop5ByIsRemovedOrderByDateCreatedDesc(boolean status);
	
	@Query(value ="SELECT distinct job_status FROM job_info",nativeQuery = true) 
	public List<String> findDistinctJobStatusOrderByDateCreatedDesc();

	public List<JobInfo> findByJobStatusOrderByDateCreatedDesc(String queryDetail);

	public List<JobInfo> findByUserAccountOrderByDateCreatedDesc(UserAccount userAccount);

	public List<JobInfo> findTop15ByIsRemovedOrderByDateCreatedDesc(boolean status);

	public List<JobInfo> findByUserAccountAndIsRemovedOrderByDateCreatedDesc(UserAccount userAccount, boolean status);
	
}
