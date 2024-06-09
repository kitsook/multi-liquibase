package net.clarenceho.multi_liquibase.modules.vessel.repositories;

import net.clarenceho.multi_liquibase.modules.vessel.models.Frigate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FrigateRepository extends JpaRepository<Frigate, Integer> {
    public List<Frigate> findAllByOrderByIdAsc();
}
