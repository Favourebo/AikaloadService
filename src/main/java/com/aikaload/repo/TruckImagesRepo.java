package com.aikaload.repo;

import com.aikaload.entity.TruckImages;
import com.aikaload.entity.TruckInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Repository
@Transactional
public interface TruckImagesRepo extends JpaRepository<TruckImages,Long> {
    List<TruckImages> findByTruckInfo(TruckInfo truckInfo);

    void deleteByTruckInfo(TruckInfo savedTruckInfo);

    @Query(value="select * from truck_images where truck_info_id =:truckId", nativeQuery = true)
    List<TruckImages> getByTruckId(@Param("truckId") Long truckId);
}
