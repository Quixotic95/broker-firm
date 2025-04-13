package com.github.quixotic95.brokerfirmchallenge.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssetId implements Serializable {
    @EqualsAndHashCode.Include
    @Column(name = "customer_id", nullable = false)
    private Long customerId;
    @EqualsAndHashCode.Include
    @Column(name = "asset_type", nullable = false)
    private String assetType;
}
