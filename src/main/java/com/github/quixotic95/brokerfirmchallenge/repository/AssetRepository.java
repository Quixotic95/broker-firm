package com.github.quixotic95.brokerfirmchallenge.repository;

import com.github.quixotic95.brokerfirmchallenge.model.Asset;
import com.github.quixotic95.brokerfirmchallenge.model.AssetId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, AssetId> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Asset a WHERE a.id.customerId = :customerId AND a.id.assetName = :assetName")
    Optional<Asset> findByIdWithLock(@Param("customerId") Long customerId, @Param("assetName") String assetName);

    List<Asset> findAllByIdCustomerId(Long customerId);

}
