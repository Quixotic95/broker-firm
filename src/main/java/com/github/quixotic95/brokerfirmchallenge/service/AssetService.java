package com.github.quixotic95.brokerfirmchallenge.service;

import com.github.quixotic95.brokerfirmchallenge.model.Asset;

import java.math.BigDecimal;
import java.util.List;

public interface AssetService {
    Asset getAssetOrThrow(Long customerId, String assetName);

    void decreaseUsableSize(Long customerId, String assetName, BigDecimal amount);

    void increaseUsableSize(Long customerId, String assetName, BigDecimal amount);

    List<Asset> getAllAssetsByCustomerId(Long customerId);

}
