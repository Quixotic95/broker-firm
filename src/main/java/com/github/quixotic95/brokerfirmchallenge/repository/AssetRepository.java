package com.github.quixotic95.brokerfirmchallenge.repository;

import com.github.quixotic95.brokerfirmchallenge.model.Asset;
import com.github.quixotic95.brokerfirmchallenge.model.AssetId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, AssetId> {

}