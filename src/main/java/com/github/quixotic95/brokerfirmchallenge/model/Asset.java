package com.github.quixotic95.brokerfirmchallenge.model;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ASSETS")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Asset {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "customerId", column = @Column(name = "customer_id")),
            @AttributeOverride(name = "assetType", column = @Column(name = "asset_type"))
    })
    private AssetId id;
    private BigDecimal size;
    private BigDecimal usableSize;
}
