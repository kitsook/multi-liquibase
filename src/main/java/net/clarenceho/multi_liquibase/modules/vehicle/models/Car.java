package net.clarenceho.multi_liquibase.modules.vehicle.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "car")
public class Car {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "MAKE", length = 255, nullable = true)
    private String make;

    @Column(name = "MODEL", length = 255, nullable = true)
    private String model;

    @Column(name = "MODEL_YEAR", nullable = true)
    private Integer modelYear;

    @Column(name = "PRICE", nullable = true)
    private Double price;
}
