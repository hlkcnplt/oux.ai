package ai.oux.core.repositories;

import ai.oux.core.entities.Annotation;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, UUID> {
}
