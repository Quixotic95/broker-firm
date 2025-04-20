package com.github.quixotic95.brokerfirmchallenge.service.impl;

import com.github.quixotic95.brokerfirmchallenge.exception.NotFoundException;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import com.github.quixotic95.brokerfirmchallenge.model.Asset;
import com.github.quixotic95.brokerfirmchallenge.model.AssetId;
import com.github.quixotic95.brokerfirmchallenge.repository.AssetRepository;
import com.github.quixotic95.brokerfirmchallenge.service.AssetService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;

    @Override
    @Transactional
    public void decreaseUsableSize(Long customerId, String assetName, BigDecimal amount) {
        Asset asset = getAssetWithLockOrThrow(customerId, assetName);
        asset.decreaseUsableSize(amount);
        assetRepository.save(asset);
    }

    @Override
    @Transactional
    public void increaseUsableSize(Long customerId, String assetName, BigDecimal amount) {
        Asset asset = getAssetWithLockOrThrow(customerId, assetName);
        asset.increaseUsableSize(amount);
        assetRepository.save(asset);
    }

    @Override
    @Transactional
    public void increaseSize(Long customerId, String assetName, BigDecimal amount) {
        Asset asset = getAssetWithLockOrThrow(customerId, assetName);
        asset.increaseSize(amount);
        assetRepository.save(asset);
    }

    public Asset getAssetOrThrow(Long customerId, String assetName) {
        AssetId id = AssetId.of(customerId, assetName);
        return assetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ASSET_NOT_FOUND));
    }

    public Asset getAssetWithLockOrThrow(Long customerId, String assetName) {
        return assetRepository.findByIdWithLock(customerId, assetName)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ASSET_NOT_FOUND));
    }

    @Override
    public List<Asset> getAllAssetsByCustomerId(Long customerId) {
        return assetRepository.findAllByIdCustomerId(customerId);
    }
}
