CREATE TABLE annotations (
    id UUID PRIMARY KEY,
    report_id UUID NOT NULL,
    canvas_x DOUBLE PRECISION NOT NULL,
    canvas_y DOUBLE PRECISION NOT NULL,
    issue TEXT NOT NULL,
    severity VARCHAR(255) NOT NULL,
    CONSTRAINT fk_annotations_report FOREIGN KEY (report_id) REFERENCES ai_reports(id) ON DELETE CASCADE
);
