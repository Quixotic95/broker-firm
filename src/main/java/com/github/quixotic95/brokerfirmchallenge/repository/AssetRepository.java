package com.github.quixotic95.brokerfirmchallenge.repository;

import com.github.quixotic95.brokerfirmchallenge.model.Asset;
import com.github.quixotic95.brokerfirmchallenge.model.AssetId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, AssetId> {
}
