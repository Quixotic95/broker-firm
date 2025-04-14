package com.github.quixotic95.brokerfirmchallenge.model;


import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "ASSETS", indexes = {@Index(name = "idx_asset_customer_asset", columnList = "customer_id,asset_type", unique = true)})
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Asset {
    @EmbeddedId
    @EqualsAndHashCode.Include
    @AttributeOverrides({@AttributeOverride(name = "customerId", column = @Column(name = "customer_id")), @AttributeOverride(name = "assetType", column = @Column(name = "asset_type"))})
    private AssetId id;

    @Version
    private Long version;

    @PositiveOrZero
    private BigDecimal size;

    @PositiveOrZero
    private BigDecimal usableSize;

}
