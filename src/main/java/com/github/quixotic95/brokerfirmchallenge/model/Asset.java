package com.github.quixotic95.brokerfirmchallenge.model;


import com.github.quixotic95.brokerfirmchallenge.exception.InsufficientException;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ASSETS")
public class Asset {

    @EmbeddedId
    private AssetId id;

    @Column(name = "asset_size", nullable = false, precision = 19, scale = 4)
    private BigDecimal size;

    @Column(name = "usable_size", nullable = false, precision = 19, scale = 4)
    private BigDecimal usableSize;

    @Version
    private Long version;

    public Asset(AssetId id, BigDecimal size, BigDecimal usableSize) {
        this.id = id;
        this.size = size;
        this.usableSize = usableSize;
    }

    public void increaseUsableSize(BigDecimal amount) {
        this.usableSize = this.usableSize.add(amount);
    }

    public void decreaseUsableSize(BigDecimal amount) {
        if (this.usableSize.compareTo(amount) < 0) {
            throw new InsufficientException(ErrorCode.INSUFFICIENT_BALANCE.getMessage());
        }
        this.usableSize = this.usableSize.subtract(amount);
    }
}
