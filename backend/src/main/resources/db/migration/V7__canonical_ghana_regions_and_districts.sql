-- V7__canonical_ghana_regions_and_districts.sql
-- Production Canonical Ghana Region & District Reference Data Reconciliation

-- 1. Ensure 16 Canonical Regions exist
INSERT INTO region_ref (id, code, name, active) VALUES
((substr(md5('GEC:REGION:AHAFO'), 1, 8) || '-' || substr(md5('GEC:REGION:AHAFO'), 9, 4) || '-' || substr(md5('GEC:REGION:AHAFO'), 13, 4) || '-' || substr(md5('GEC:REGION:AHAFO'), 17, 4) || '-' || substr(md5('GEC:REGION:AHAFO'), 21, 12))::uuid, 'AHAFO', 'Ahafo Region', TRUE),
((substr(md5('GEC:REGION:ASHANTI'), 1, 8) || '-' || substr(md5('GEC:REGION:ASHANTI'), 9, 4) || '-' || substr(md5('GEC:REGION:ASHANTI'), 13, 4) || '-' || substr(md5('GEC:REGION:ASHANTI'), 17, 4) || '-' || substr(md5('GEC:REGION:ASHANTI'), 21, 12))::uuid, 'ASHANTI', 'Ashanti Region', TRUE),
((substr(md5('GEC:REGION:BONO'), 1, 8) || '-' || substr(md5('GEC:REGION:BONO'), 9, 4) || '-' || substr(md5('GEC:REGION:BONO'), 13, 4) || '-' || substr(md5('GEC:REGION:BONO'), 17, 4) || '-' || substr(md5('GEC:REGION:BONO'), 21, 12))::uuid, 'BONO', 'Bono Region', TRUE),
((substr(md5('GEC:REGION:BONO_EAST'), 1, 8) || '-' || substr(md5('GEC:REGION:BONO_EAST'), 9, 4) || '-' || substr(md5('GEC:REGION:BONO_EAST'), 13, 4) || '-' || substr(md5('GEC:REGION:BONO_EAST'), 17, 4) || '-' || substr(md5('GEC:REGION:BONO_EAST'), 21, 12))::uuid, 'BONO_EAST', 'Bono East Region', TRUE),
((substr(md5('GEC:REGION:CENTRAL'), 1, 8) || '-' || substr(md5('GEC:REGION:CENTRAL'), 9, 4) || '-' || substr(md5('GEC:REGION:CENTRAL'), 13, 4) || '-' || substr(md5('GEC:REGION:CENTRAL'), 17, 4) || '-' || substr(md5('GEC:REGION:CENTRAL'), 21, 12))::uuid, 'CENTRAL', 'Central Region', TRUE),
((substr(md5('GEC:REGION:EASTERN'), 1, 8) || '-' || substr(md5('GEC:REGION:EASTERN'), 9, 4) || '-' || substr(md5('GEC:REGION:EASTERN'), 13, 4) || '-' || substr(md5('GEC:REGION:EASTERN'), 17, 4) || '-' || substr(md5('GEC:REGION:EASTERN'), 21, 12))::uuid, 'EASTERN', 'Eastern Region', TRUE),
((substr(md5('GEC:REGION:GREATER_ACCRA'), 1, 8) || '-' || substr(md5('GEC:REGION:GREATER_ACCRA'), 9, 4) || '-' || substr(md5('GEC:REGION:GREATER_ACCRA'), 13, 4) || '-' || substr(md5('GEC:REGION:GREATER_ACCRA'), 17, 4) || '-' || substr(md5('GEC:REGION:GREATER_ACCRA'), 21, 12))::uuid, 'GREATER_ACCRA', 'Greater Accra Region', TRUE),
((substr(md5('GEC:REGION:NORTH_EAST'), 1, 8) || '-' || substr(md5('GEC:REGION:NORTH_EAST'), 9, 4) || '-' || substr(md5('GEC:REGION:NORTH_EAST'), 13, 4) || '-' || substr(md5('GEC:REGION:NORTH_EAST'), 17, 4) || '-' || substr(md5('GEC:REGION:NORTH_EAST'), 21, 12))::uuid, 'NORTH_EAST', 'North East Region', TRUE),
((substr(md5('GEC:REGION:NORTHERN'), 1, 8) || '-' || substr(md5('GEC:REGION:NORTHERN'), 9, 4) || '-' || substr(md5('GEC:REGION:NORTHERN'), 13, 4) || '-' || substr(md5('GEC:REGION:NORTHERN'), 17, 4) || '-' || substr(md5('GEC:REGION:NORTHERN'), 21, 12))::uuid, 'NORTHERN', 'Northern Region', TRUE),
((substr(md5('GEC:REGION:OTI'), 1, 8) || '-' || substr(md5('GEC:REGION:OTI'), 9, 4) || '-' || substr(md5('GEC:REGION:OTI'), 13, 4) || '-' || substr(md5('GEC:REGION:OTI'), 17, 4) || '-' || substr(md5('GEC:REGION:OTI'), 21, 12))::uuid, 'OTI', 'Oti Region', TRUE),
((substr(md5('GEC:REGION:SAVANNAH'), 1, 8) || '-' || substr(md5('GEC:REGION:SAVANNAH'), 9, 4) || '-' || substr(md5('GEC:REGION:SAVANNAH'), 13, 4) || '-' || substr(md5('GEC:REGION:SAVANNAH'), 17, 4) || '-' || substr(md5('GEC:REGION:SAVANNAH'), 21, 12))::uuid, 'SAVANNAH', 'Savannah Region', TRUE),
((substr(md5('GEC:REGION:UPPER_EAST'), 1, 8) || '-' || substr(md5('GEC:REGION:UPPER_EAST'), 9, 4) || '-' || substr(md5('GEC:REGION:UPPER_EAST'), 13, 4) || '-' || substr(md5('GEC:REGION:UPPER_EAST'), 17, 4) || '-' || substr(md5('GEC:REGION:UPPER_EAST'), 21, 12))::uuid, 'UPPER_EAST', 'Upper East Region', TRUE),
((substr(md5('GEC:REGION:UPPER_WEST'), 1, 8) || '-' || substr(md5('GEC:REGION:UPPER_WEST'), 9, 4) || '-' || substr(md5('GEC:REGION:UPPER_WEST'), 13, 4) || '-' || substr(md5('GEC:REGION:UPPER_WEST'), 17, 4) || '-' || substr(md5('GEC:REGION:UPPER_WEST'), 21, 12))::uuid, 'UPPER_WEST', 'Upper West Region', TRUE),
((substr(md5('GEC:REGION:VOLTA'), 1, 8) || '-' || substr(md5('GEC:REGION:VOLTA'), 9, 4) || '-' || substr(md5('GEC:REGION:VOLTA'), 13, 4) || '-' || substr(md5('GEC:REGION:VOLTA'), 17, 4) || '-' || substr(md5('GEC:REGION:VOLTA'), 21, 12))::uuid, 'VOLTA', 'Volta Region', TRUE),
((substr(md5('GEC:REGION:WESTERN'), 1, 8) || '-' || substr(md5('GEC:REGION:WESTERN'), 9, 4) || '-' || substr(md5('GEC:REGION:WESTERN'), 13, 4) || '-' || substr(md5('GEC:REGION:WESTERN'), 17, 4) || '-' || substr(md5('GEC:REGION:WESTERN'), 21, 12))::uuid, 'WESTERN', 'Western Region', TRUE),
((substr(md5('GEC:REGION:WESTERN_NORTH'), 1, 8) || '-' || substr(md5('GEC:REGION:WESTERN_NORTH'), 9, 4) || '-' || substr(md5('GEC:REGION:WESTERN_NORTH'), 13, 4) || '-' || substr(md5('GEC:REGION:WESTERN_NORTH'), 17, 4) || '-' || substr(md5('GEC:REGION:WESTERN_NORTH'), 21, 12))::uuid, 'WESTERN_NORTH', 'Western North Region', TRUE)
ON CONFLICT (code) DO UPDATE SET name = EXCLUDED.name, active = TRUE;

-- 2. Upsert District Reference Data using Deterministic UUIDs from md5('GEC:DISTRICT:' || code)
INSERT INTO district_ref (id, region_id, code, name, assembly_type, active)
SELECT
    (substr(md5('GEC:DISTRICT:' || d.code), 1, 8) || '-' || substr(md5('GEC:DISTRICT:' || d.code), 9, 4) || '-' || substr(md5('GEC:DISTRICT:' || d.code), 13, 4) || '-' || substr(md5('GEC:DISTRICT:' || d.code), 17, 4) || '-' || substr(md5('GEC:DISTRICT:' || d.code), 21, 12))::uuid AS id,
    r.id AS region_id,
    d.code,
    d.name,
    d.assembly_type,
    TRUE AS active
FROM (
    VALUES
    -- GREATER ACCRA REGION
    ('GREATER_ACCRA', 'ACC_METRO', 'Accra Metropolitan', 'METROPOLITAN'),
    ('GREATER_ACCRA', 'TEMA_METRO', 'Tema Metropolitan', 'METROPOLITAN'),
    ('GREATER_ACCRA', 'GA_EAST', 'Ga East Municipal', 'MUNICIPAL'),
    ('GREATER_ACCRA', 'GA_WEST', 'Ga West Municipal', 'MUNICIPAL'),
    ('GREATER_ACCRA', 'GA_SOUTH', 'Ga South Municipal', 'MUNICIPAL'),
    ('GREATER_ACCRA', 'ADENTA_MUN', 'Adentan Municipal', 'MUNICIPAL'),
    ('GREATER_ACCRA', 'LA_DADE_KOTOPON', 'La Dade-Kotopon Municipal', 'MUNICIPAL'),
    ('GREATER_ACCRA', 'KROWOR_MUN', 'Krowor Municipal', 'MUNICIPAL'),

    -- CENTRAL REGION (ALL 22 MMDA DISTRICTS)
    ('CENTRAL', 'CAPE_COAST_METRO', 'Cape Coast Metropolitan', 'METROPOLITAN'),
    ('CENTRAL', 'KOMENDA_EDINA', 'Komenda-Edina-Eguafo-Abirem Municipal', 'MUNICIPAL'),
    ('CENTRAL', 'MFANTSIMAN_MUN', 'Mfantsiman Municipal', 'MUNICIPAL'),
    ('CENTRAL', 'ABURA_ASEBU_KWAMANKESE', 'Abura-Asebu-Kwamankese District', 'DISTRICT'),
    ('CENTRAL', 'AJUMAKO_ENYAN_ESSIAM', 'Ajumako-Enyan-Essiam District', 'DISTRICT'),
    ('CENTRAL', 'AGONA_EAST', 'Agona East District', 'DISTRICT'),
    ('CENTRAL', 'AGONA_WEST_MUN', 'Agona West Municipal', 'MUNICIPAL'),
    ('CENTRAL', 'ASSIN_FOSO_MUN', 'Assin Foso Municipal', 'MUNICIPAL'),
    ('CENTRAL', 'ASSIN_NORTH', 'Assin North District', 'DISTRICT'),
    ('CENTRAL', 'ASSIN_SOUTH', 'Assin South District', 'DISTRICT'),
    ('CENTRAL', 'AWUTU_SENYA_EAST', 'Awutu Senya East Municipal', 'MUNICIPAL'),
    ('CENTRAL', 'AWUTU_SENYA_WEST', 'Awutu Senya West District', 'DISTRICT'),
    ('CENTRAL', 'EFFUTU_MUN', 'Effutu Municipal', 'MUNICIPAL'),
    ('CENTRAL', 'EKUMFI_DISTRICT', 'Ekumfi District', 'DISTRICT'),
    ('CENTRAL', 'GOMOA_CENTRAL', 'Gomoa Central District', 'DISTRICT'),
    ('CENTRAL', 'GOMOA_EAST', 'Gomoa East District', 'DISTRICT'),
    ('CENTRAL', 'GOMOA_WEST', 'Gomoa West District', 'DISTRICT'),
    ('CENTRAL', 'TWIFO_ATI_MORKWA', 'Twifo Ati-Morkwa District', 'DISTRICT'),
    ('CENTRAL', 'TWIFO_HEMANG_LOWER', 'Twifo Hemang Lower Denkyira District', 'DISTRICT'),
    ('CENTRAL', 'UPPER_DENKYIRA_EAST', 'Upper Denkyira East Municipal', 'MUNICIPAL'),
    ('CENTRAL', 'UPPER_DENKYIRA_WEST', 'Upper Denkyira West District', 'DISTRICT'),
    ('CENTRAL', 'ASIKUMA_ODOBEN_BRAKWA', 'Asikuma-Odoben-Brakwa District', 'DISTRICT'),

    -- ASHANTI REGION
    ('ASHANTI', 'KUMASI_METRO', 'Kumasi Metropolitan', 'METROPOLITAN'),
    ('ASHANTI', 'EJISU_MUN', 'Ejisu Municipal', 'MUNICIPAL'),
    ('ASHANTI', 'OBUASI_MUN', 'Obuasi Municipal', 'MUNICIPAL'),

    -- WESTERN REGION
    ('WESTERN', 'SEKONDI_TAKORADI', 'Sekondi Takoradi Metropolitan', 'METROPOLITAN'),
    ('WESTERN', 'TARKWA_NSUAEM', 'Tarkwa Nsuaem Municipal', 'MUNICIPAL'),

    -- NORTHERN REGION
    ('NORTHERN', 'TAMALE_METRO', 'Tamale Metropolitan', 'METROPOLITAN'),
    ('NORTHERN', 'SAVELUGU_MUN', 'Savelugu Municipal', 'MUNICIPAL'),

    -- VOLTA REGION
    ('VOLTA', 'HO_MUN', 'Ho Municipal', 'MUNICIPAL'),
    ('VOLTA', 'KETA_MUN', 'Keta Municipal', 'MUNICIPAL')
) AS d(region_code, code, name, assembly_type)
JOIN region_ref r ON r.code = d.region_code
ON CONFLICT (code) DO UPDATE SET
    region_id = EXCLUDED.region_id,
    name = EXCLUDED.name,
    assembly_type = EXCLUDED.assembly_type,
    active = TRUE;

-- 3. Fail-Fast Verification Block
DO $$
DECLARE
    r_count INTEGER;
    d_count INTEGER;
    dup_code INTEGER;
    orphan_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO r_count FROM region_ref WHERE active = TRUE;
    IF r_count < 16 THEN
        RAISE EXCEPTION 'Flyway V7 Assertion Failed: Expected at least 16 active regions, found %', r_count;
    END IF;

    SELECT COUNT(*) INTO d_count FROM district_ref WHERE active = TRUE;
    IF d_count < 20 THEN
        RAISE EXCEPTION 'Flyway V7 Assertion Failed: Expected active districts, found %', d_count;
    END IF;

    SELECT COUNT(*) INTO dup_code FROM (
        SELECT code FROM district_ref GROUP BY code HAVING COUNT(*) > 1
    ) t;
    IF dup_code > 0 THEN
        RAISE EXCEPTION 'Flyway V7 Assertion Failed: Found % duplicate district codes', dup_code;
    END IF;

    SELECT COUNT(*) INTO orphan_count
    FROM district_ref d LEFT JOIN region_ref r ON r.id = d.region_id WHERE r.id IS NULL;
    IF orphan_count > 0 THEN
        RAISE EXCEPTION 'Flyway V7 Assertion Failed: Found % orphan district references', orphan_count;
    END IF;
END $$;
