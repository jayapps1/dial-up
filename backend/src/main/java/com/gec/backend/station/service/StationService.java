package com.gec.backend.station.service;

import com.gec.backend.admin.dto.*;
import com.gec.backend.admin.security.AdminScopeService;
import com.gec.backend.admin.service.AuditService;
import com.gec.backend.common.exception.ResourceNotFoundException;
import com.gec.backend.station.dto.StationContactResponse;
import com.gec.backend.station.dto.StationDetailsResponse;
import com.gec.backend.station.dto.StationResponse;
import com.gec.backend.station.entity.StationContactEntity;
import com.gec.backend.station.entity.StationEntity;
import com.gec.backend.station.mapper.StationMapper;
import com.gec.backend.station.repository.StationContactRepository;
import com.gec.backend.station.repository.StationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StationService {

    private final StationRepository stationRepository;
    private final StationContactRepository contactRepository;
    private final StationMapper stationMapper;
    private final AdminScopeService scopeService;
    private final AuditService auditService;

    public StationService(
        StationRepository stationRepository,
        StationContactRepository contactRepository,
        StationMapper stationMapper,
        AdminScopeService scopeService,
        AuditService auditService
    ) {
        this.stationRepository = stationRepository;
        this.contactRepository = contactRepository;
        this.stationMapper = stationMapper;
        this.scopeService = scopeService;
        this.auditService = auditService;
    }

    public List<StationResponse> getActiveVerifiedStationsByAgency(UUID agencyId) {
        return stationRepository.findByAgencyIdAndActiveTrueAndVerifiedTrue(agencyId)
            .stream()
            .map(stationMapper::toResponse)
            .toList();
    }

    public Page<StationResponse> getAdminStations(Pageable pageable) {
        if (scopeService.isSystemAdmin()) {
            return stationRepository.findAll(pageable).map(stationMapper::toResponse);
        } else {
            UUID agencyId = scopeService.getCurrentPrincipal().getAgencyId();
            return stationRepository.findByAgencyId(agencyId, pageable).map(stationMapper::toResponse);
        }
    }

    public List<StationResponse> searchStations(String query) {
        String q = query == null ? "" : query.trim();
        return stationRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(q, q)
            .stream()
            .map(stationMapper::toResponse)
            .toList();
    }

    public StationDetailsResponse getStationDetails(UUID stationId) {
        StationEntity stationEntity = stationRepository.findById(stationId)
            .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + stationId));

        scopeService.validateAgencyScope(stationEntity.getAgencyId());

        StationResponse stationResponse = stationMapper.toResponse(stationEntity);
        List<StationContactResponse> contacts = contactRepository
            .findByStationIdOrderByPriorityAsc(stationId)
            .stream()
            .map(stationMapper::toContactResponse)
            .toList();

        return new StationDetailsResponse(stationResponse, contacts);
    }

    public List<StationContactResponse> getStationContacts(UUID stationId) {
        StationEntity stationEntity = stationRepository.findById(stationId)
            .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + stationId));

        scopeService.validateAgencyScope(stationEntity.getAgencyId());

        return contactRepository
            .findByStationIdOrderByPriorityAsc(stationId)
            .stream()
            .map(stationMapper::toContactResponse)
            .toList();
    }

    @Transactional
    public StationResponse createStation(CreateStationRequest request) {
        scopeService.validateAgencyScope(request.agencyId());

        UUID stationId = UUID.randomUUID();
        StationEntity station = new StationEntity(
            stationId,
            request.agencyId(),
            request.name(),
            request.code(),
            request.region(),
            request.district(),
            request.locality(),
            request.latitude(),
            request.longitude(),
            request.locationAccuracyMeters(),
            true,
            false
        );
        stationRepository.save(station);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        auditService.logAction(actorId, "STATION_CREATED", "STATION", stationId, request.agencyId(), "Created station " + station.getName(), null, null);

        return stationMapper.toResponse(station);
    }

    @Transactional
    public StationResponse updateStation(UUID stationId, UpdateStationRequest request) {
        StationEntity station = stationRepository.findById(stationId)
            .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + stationId));

        scopeService.validateAgencyScope(station.getAgencyId());

        station.setName(request.name());
        station.setRegion(request.region());
        station.setDistrict(request.district());
        station.setLocality(request.locality());
        station.setLatitude(request.latitude());
        station.setLongitude(request.longitude());
        station.setLocationAccuracyMeters(request.locationAccuracyMeters());
        station.setUpdatedAt(LocalDateTime.now());

        stationRepository.save(station);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        auditService.logAction(actorId, "STATION_UPDATED", "STATION", stationId, station.getAgencyId(), "Updated station " + station.getName(), null, null);

        return stationMapper.toResponse(station);
    }

    @Transactional
    public StationResponse verifyStation(UUID stationId) {
        StationEntity station = stationRepository.findById(stationId)
            .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + stationId));

        station.setVerified(true);
        station.setUpdatedAt(LocalDateTime.now());
        stationRepository.save(station);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        auditService.logAction(actorId, "STATION_VERIFIED", "STATION", stationId, station.getAgencyId(), "Verified station " + station.getName(), null, null);

        return stationMapper.toResponse(station);
    }

    @Transactional
    public StationResponse toggleStationStatus(UUID stationId) {
        StationEntity station = stationRepository.findById(stationId)
            .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + stationId));

        scopeService.validateAgencyScope(station.getAgencyId());

        station.setActive(!station.isActive());
        station.setUpdatedAt(LocalDateTime.now());
        stationRepository.save(station);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        auditService.logAction(actorId, "STATION_STATUS_TOGGLED", "STATION", stationId, station.getAgencyId(), "Toggled active status to " + station.isActive() + " for " + station.getName(), null, null);

        return stationMapper.toResponse(station);
    }

    @Transactional
    public StationContactResponse createStationContact(UUID stationId, CreateStationContactRequest request) {
        StationEntity station = stationRepository.findById(stationId)
            .orElseThrow(() -> new ResourceNotFoundException("Station not found: " + stationId));

        scopeService.validateAgencyScope(station.getAgencyId());

        UUID contactId = UUID.randomUUID();
        StationContactEntity contact = new StationContactEntity(
            contactId,
            stationId,
            request.phoneNumber(),
            request.type(),
            request.priority(),
            request.publicContact(),
            request.available24Hour(),
            true,
            false
        );
        contactRepository.save(contact);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        auditService.logAction(actorId, "STATION_CONTACT_CREATED", "STATION_CONTACT", contactId, station.getAgencyId(), "Created contact " + contact.getPhoneNumber() + " for station " + station.getName(), null, null);

        return stationMapper.toContactResponse(contact);
    }

    @Transactional
    public StationContactResponse updateStationContact(UUID contactId, UpdateStationContactRequest request) {
        StationContactEntity contact = contactRepository.findById(contactId)
            .orElseThrow(() -> new ResourceNotFoundException("Station contact not found: " + contactId));

        StationEntity station = stationRepository.findById(contact.getStationId())
            .orElseThrow(() -> new ResourceNotFoundException("Station not found for contact: " + contactId));

        scopeService.validateAgencyScope(station.getAgencyId());

        contact.setPhoneNumber(request.phoneNumber());
        contact.setContactType(request.type());
        contact.setPriority(request.priority());
        contact.setPublicContact(request.publicContact());
        contact.setAvailable24Hour(request.available24Hour());
        contact.setUpdatedAt(LocalDateTime.now());

        contactRepository.save(contact);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        auditService.logAction(actorId, "STATION_CONTACT_UPDATED", "STATION_CONTACT", contactId, station.getAgencyId(), "Updated contact " + contact.getPhoneNumber(), null, null);

        return stationMapper.toContactResponse(contact);
    }

    @Transactional
    public StationContactResponse verifyStationContact(UUID contactId) {
        StationContactEntity contact = contactRepository.findById(contactId)
            .orElseThrow(() -> new ResourceNotFoundException("Station contact not found: " + contactId));

        StationEntity station = stationRepository.findById(contact.getStationId())
            .orElseThrow(() -> new ResourceNotFoundException("Station not found for contact: " + contactId));

        contact.setVerified(true);
        contact.setUpdatedAt(LocalDateTime.now());
        contactRepository.save(contact);

        UUID actorId = scopeService.getCurrentPrincipal().getId();
        auditService.logAction(actorId, "STATION_CONTACT_VERIFIED", "STATION_CONTACT", contactId, station.getAgencyId(), "Verified contact " + contact.getPhoneNumber(), null, null);

        return stationMapper.toContactResponse(contact);
    }
}
