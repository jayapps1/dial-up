package com.gec.backend.reference.controller;

import com.gec.backend.reference.entity.DistrictRefEntity;
import com.gec.backend.reference.entity.RegionRefEntity;
import com.gec.backend.reference.repository.DistrictRefRepository;
import com.gec.backend.reference.repository.RegionRefRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/reference")
public class ReferenceDataController {

    private final RegionRefRepository regionRepository;
    private final DistrictRefRepository districtRepository;

    public ReferenceDataController(RegionRefRepository regionRepository, DistrictRefRepository districtRepository) {
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
    }

    @GetMapping("/regions")
    public ResponseEntity<List<RegionRefEntity>> getRegions() {
        return ResponseEntity.ok(regionRepository.findByActiveTrueOrderByNameAsc());
    }

    @GetMapping("/regions/{regionId}/districts")
    public ResponseEntity<List<DistrictRefEntity>> getDistrictsByRegion(@PathVariable UUID regionId) {
        return ResponseEntity.ok(districtRepository.findByRegionIdAndActiveTrueOrderByNameAsc(regionId));
    }

    @GetMapping("/districts")
    public ResponseEntity<List<DistrictRefEntity>> searchDistricts(
        @RequestParam UUID regionId,
        @RequestParam(required = false) String search
    ) {
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(districtRepository.findByRegionIdAndNameContainingIgnoreCaseAndActiveTrueOrderByNameAsc(regionId, search.trim()));
        }
        return ResponseEntity.ok(districtRepository.findByRegionIdAndActiveTrueOrderByNameAsc(regionId));
    }
}
