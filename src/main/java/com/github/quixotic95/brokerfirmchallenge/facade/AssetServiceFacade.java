package com.github.quixotic95.brokerfirmchallenge.facade;

import com.github.quixotic95.brokerfirmchallenge.model.Asset;
import com.github.quixotic95.brokerfirmchallenge.repository.AssetRepository;
import com.github.quixotic95.brokerfirmchallenge.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AssetServiceFacade {

    private final AssetRepository assetRepository;

    public List<Asset> getAssetsForCurrentUser() {
        if (SecurityUtil.isAdmin()) {
            return assetRepository.findAll();
        } else {
            return assetRepository.findAllByIdCustomerId(SecurityUtil.getCustomerId());
        }
    }
}
