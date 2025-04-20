package com.github.quixotic95.brokerfirmchallenge.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.quixotic95.brokerfirmchallenge.model.Asset;
import com.github.quixotic95.brokerfirmchallenge.model.AssetId;
import com.github.quixotic95.brokerfirmchallenge.repository.AssetRepository;
import com.github.quixotic95.brokerfirmchallenge.security.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class AssetServiceFacadeTest {

    private final AssetRepository assetRepository = mock(AssetRepository.class);
    private final AssetServiceFacade assetServiceFacade = new AssetServiceFacade(assetRepository);

    @Test
    void getAssetsForCurrentUser_shouldReturnAllAssets_forAdmin() {
        List<Asset> allAssets = List.of(new Asset(new AssetId(1L, "AAPL"), new BigDecimal("100"), new BigDecimal("100")));

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::isAdmin)
                    .thenReturn(true);
            when(assetRepository.findAll()).thenReturn(allAssets);

            List<Asset> result = assetServiceFacade.getAssetsForCurrentUser();

            assertThat(result).containsExactlyElementsOf(allAssets);
            verify(assetRepository).findAll();
        }
    }

    @Test
    void getAssetsForCurrentUser_shouldReturnCustomerAssets_forCustomer() {
        long customerId = 42L;
        List<Asset> customerAssets = List.of(new Asset(new AssetId(customerId, "GOOG"), new BigDecimal("50"), new BigDecimal("50")));

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::isAdmin)
                    .thenReturn(false);
            securityUtil.when(SecurityUtil::getCustomerId)
                    .thenReturn(customerId);
            when(assetRepository.findAllByIdCustomerId(customerId)).thenReturn(customerAssets);

            List<Asset> result = assetServiceFacade.getAssetsForCurrentUser();

            assertThat(result).containsExactlyElementsOf(customerAssets);
            verify(assetRepository).findAllByIdCustomerId(customerId);
        }
    }
}
