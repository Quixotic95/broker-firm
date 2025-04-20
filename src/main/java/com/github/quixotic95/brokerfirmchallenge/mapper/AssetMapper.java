package com.github.quixotic95.brokerfirmchallenge.mapper;

import com.github.quixotic95.brokerfirmchallenge.dto.response.AssetDto;
import com.github.quixotic95.brokerfirmchallenge.model.Asset;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AssetMapper {
    public AssetDto toDto(Asset asset) {
        return new AssetDto(asset.getId()
                .getCustomerId(), asset.getId()
                .getAssetName(), asset.getSize(), asset.getUsableSize());
    }

    public List<AssetDto> toDtoList(List<Asset> assets) {
        return assets.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
