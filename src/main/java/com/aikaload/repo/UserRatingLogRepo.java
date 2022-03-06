package com.aikaload.repo;

import com.aikaload.entity.JobInfo;
import com.aikaload.entity.UserRatingLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRatingLogRepo extends JpaRepository<UserRatingLog,Long> {
	public UserRatingLog findByJobInfo(JobInfo jobInfo);

	@Query(value="SELECT AVG(user_rating) FROM user_rating_log  WHERE user_account_id =:userId", nativeQuery=true)
	public Double  getAverageRatingsByUserId(@Param("userId") Long userId);
 }
