package com.github.quixotic95.brokerfirmchallenge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class AssetId implements Serializable {
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    @Column(name = "asset_name", nullable = false, length = 50)
    private String assetName;

    public AssetId(Long customerId, String assetName) {
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.assetName = Objects.requireNonNull(assetName, "assetName must not be null");
    }

    public static AssetId of(Long customerId, String assetName) {
        return new AssetId(customerId, assetName);
    }
}
