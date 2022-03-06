package com.aikaload.repo;

import com.aikaload.entity.TruckInfo;
import com.aikaload.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TruckInfoRepo extends PagingAndSortingRepository<TruckInfo,Integer> {
    List<TruckInfo> findByTruckStatus(int truckStatus);

    List<TruckInfo> findByUserAccount(UserAccount u);
}
