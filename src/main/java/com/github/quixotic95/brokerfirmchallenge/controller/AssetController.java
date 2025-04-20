package com.github.quixotic95.brokerfirmchallenge.controller;

import com.github.quixotic95.brokerfirmchallenge.aspect.AuthorizeEndpoint;
import com.github.quixotic95.brokerfirmchallenge.dto.response.AssetDto;
import com.github.quixotic95.brokerfirmchallenge.facade.AssetServiceFacade;
import com.github.quixotic95.brokerfirmchallenge.mapper.AssetMapper;
import com.github.quixotic95.brokerfirmchallenge.model.Asset;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Assets", description = "Asset listing operations per customer")
public class AssetController {

    private final AssetMapper assetMapper;
    private final AssetServiceFacade assetServiceFacade;

    @Operation(
            summary = "List assets", description = "List all assets of the authenticated user. Admins see all customer assets.")
    @ApiResponses(
            {@ApiResponse(responseCode = "200", description = "Assets listed successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")})
    @GetMapping
    @AuthorizeEndpoint(customerAccessible = true)
    public ResponseEntity<List<AssetDto>> getAssets() {
        List<Asset> assets = assetServiceFacade.getAssetsForCurrentUser();
        return ResponseEntity.ok(assetMapper.toDtoList(assets));
    }
}