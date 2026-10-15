package com.gec.backend.reference.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "district_ref")
public class DistrictRefEntity {

    @Id
    private UUID id;

    @Column(name = "region_id", nullable = false)
    private UUID regionId;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "assembly_type", nullable = false)
    private String assemblyType;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public DistrictRefEntity() {}

    public DistrictRefEntity(UUID id, UUID regionId, String code, String name, String assemblyType, boolean active) {
        this.id = id;
        this.regionId = regionId;
        this.code = code;
        this.name = name;
        this.assemblyType = assemblyType;
        this.active = active;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRegionId() { return regionId; }
    public void setRegionId(UUID regionId) { this.regionId = regionId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAssemblyType() { return assemblyType; }
    public void setAssemblyType(String assemblyType) { this.assemblyType = assemblyType; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
