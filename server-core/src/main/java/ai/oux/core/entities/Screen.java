package ai.oux.core.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "screens")
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "version_tag", columnDefinition = "TEXT")
    private String versionTag;

    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @Column(name = "canvas_x")
    private double canvasX;

    @Column(name = "canvas_y")
    private double canvasY;

    @Column(name = "canvas_scale")
    private double canvasScale = 1.0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AiReport> reports;

    public UUID getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public UUID getProjectId() {
        return project != null ? project.getId() : null;
    }

    public String getVersionTag() {
        return versionTag;
    }

    public void setVersionTag(String versionTag) {
        this.versionTag = versionTag;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getCanvasX() {
        return canvasX;
    }

    public void setCanvasX(double canvasX) {
        this.canvasX = canvasX;
    }

    public double getCanvasY() {
        return canvasY;
    }

    public void setCanvasY(double canvasY) {
        this.canvasY = canvasY;
    }

    public double getCanvasScale() {
        return canvasScale;
    }

    public void setCanvasScale(double canvasScale) {
        this.canvasScale = canvasScale;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<AiReport> getReports() {
        return reports;
    }

    public void setReports(List<AiReport> reports) {
        this.reports = reports;
    }
}
