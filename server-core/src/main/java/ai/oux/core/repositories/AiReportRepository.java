package ai.oux.core.repositories;

import ai.oux.core.entities.AiReport;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiReportRepository extends JpaRepository<AiReport, UUID> {
}
