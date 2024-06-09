package net.clarenceho.multi_liquibase.modules.vehicle.repositories;

import net.clarenceho.multi_liquibase.modules.vehicle.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {
    public List<Car> findAllByOrderByIdAsc();
}
