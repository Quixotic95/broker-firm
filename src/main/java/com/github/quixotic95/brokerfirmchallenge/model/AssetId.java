package com.github.quixotic95.brokerfirmchallenge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class AssetId implements Serializable {
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    @Column(name = "asset_name", nullable = false)
    private String assetName;
}
