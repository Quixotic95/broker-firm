package com.github.quixotic95.brokerfirmchallenge.dto.response;

import com.github.quixotic95.brokerfirmchallenge.enums.CustomerRole;

public record CustomerDto(
        Long id,
        String username,
        CustomerRole role
) {
}
