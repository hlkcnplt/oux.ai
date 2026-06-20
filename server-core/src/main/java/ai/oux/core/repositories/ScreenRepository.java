package ai.oux.core.repositories;

import ai.oux.core.entities.Screen;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, UUID> {
    List<Screen> findByProject_Id(UUID projectId);
}
