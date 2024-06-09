package net.clarenceho.multi_liquibase.modules.vessel.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "frigate")
public class Frigate {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "COUNTRY", length = 255, nullable = true)
    private String country;

    @Column(name = "VESSEL_CLASS", length = 255, nullable = true)
    private String vesselClass;

    @Column(name = "MAX_SPEED", nullable = true)
    private Double maxSpeed;
}
