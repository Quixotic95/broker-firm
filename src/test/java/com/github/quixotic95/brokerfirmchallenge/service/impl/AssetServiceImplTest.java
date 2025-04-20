package com.github.quixotic95.brokerfirmchallenge.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.quixotic95.brokerfirmchallenge.exception.NotFoundException;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import com.github.quixotic95.brokerfirmchallenge.model.Asset;
import com.github.quixotic95.brokerfirmchallenge.model.AssetId;
import com.github.quixotic95.brokerfirmchallenge.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class AssetServiceImplTest {

    private final Long customerId = 1L;
    private final String assetName = "AAPL";
    private final BigDecimal amount = BigDecimal.valueOf(10);
    @Mock
    private AssetRepository assetRepository;
    @InjectMocks
    private AssetServiceImpl assetService;
    private Asset mockAsset;

    @BeforeEach
    void setUp() {
        AssetId id = AssetId.of(customerId, assetName);
        mockAsset = new Asset(id, BigDecimal.valueOf(100), BigDecimal.valueOf(80));
    }

    @Test
    void decreaseUsableSize_success() {
        when(assetRepository.findByIdWithLock(customerId, assetName)).thenReturn(Optional.of(mockAsset));

        assetService.decreaseUsableSize(customerId, assetName, amount);

        verify(assetRepository).save(mockAsset);
        assertEquals(BigDecimal.valueOf(70), mockAsset.getUsableSize());
    }

    @Test
    void increaseUsableSize_success() {
        when(assetRepository.findByIdWithLock(customerId, assetName)).thenReturn(Optional.of(mockAsset));

        assetService.increaseUsableSize(customerId, assetName, amount);

        verify(assetRepository).save(mockAsset);
        assertEquals(BigDecimal.valueOf(90), mockAsset.getUsableSize());
    }

    @Test
    void increaseSize_success() {
        when(assetRepository.findByIdWithLock(customerId, assetName)).thenReturn(Optional.of(mockAsset));

        assetService.increaseSize(customerId, assetName, amount);

        verify(assetRepository).save(mockAsset);
        assertEquals(BigDecimal.valueOf(110), mockAsset.getSize());
        assertEquals(BigDecimal.valueOf(90), mockAsset.getUsableSize());
    }

    @Test
    void getAssetOrThrow_found() {
        AssetId id = AssetId.of(customerId, assetName);
        when(assetRepository.findById(id)).thenReturn(Optional.of(mockAsset));

        Asset result = assetService.getAssetOrThrow(customerId, assetName);

        assertNotNull(result);
        assertEquals(mockAsset, result);
    }

    @Test
    void getAssetOrThrow_notFound() {
        AssetId id = AssetId.of(customerId, assetName);
        when(assetRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> assetService.getAssetOrThrow(customerId, assetName));
        assertEquals(ErrorCode.ASSET_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAssetWithLockOrThrow_found() {
        when(assetRepository.findByIdWithLock(customerId, assetName)).thenReturn(Optional.of(mockAsset));

        Asset result = assetService.getAssetWithLockOrThrow(customerId, assetName);

        assertNotNull(result);
        assertEquals(mockAsset, result);
    }

    @Test
    void getAssetWithLockOrThrow_notFound() {
        when(assetRepository.findByIdWithLock(customerId, assetName)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> assetService.getAssetWithLockOrThrow(customerId, assetName));
        assertEquals(ErrorCode.ASSET_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void getAllAssetsByCustomerId_shouldReturnList() {
        when(assetRepository.findAllByIdCustomerId(customerId)).thenReturn(List.of(mockAsset));

        List<Asset> result = assetService.getAllAssetsByCustomerId(customerId);

        assertEquals(1, result.size());
        assertEquals(mockAsset, result.get(0));
    }
}