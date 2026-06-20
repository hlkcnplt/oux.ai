package ai.oux.core.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "annotations")
public class Annotation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private AiReport report;

    @Column(name = "canvas_x", nullable = false)
    private double canvasX;

    @Column(name = "canvas_y", nullable = false)
    private double canvasY;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String issue;

    @Column(nullable = false)
    private String severity;

    public UUID getId() {
        return id;
    }

    public AiReport getReport() {
        return report;
    }

    public void setReport(AiReport report) {
        this.report = report;
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

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
