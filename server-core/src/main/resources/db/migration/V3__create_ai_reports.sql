CREATE TABLE ai_reports (
    id UUID PRIMARY KEY,
    screen_id UUID NOT NULL,
    provider_name VARCHAR(255) NOT NULL,
    model_version VARCHAR(255) NOT NULL,
    raw_response JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ai_reports_screen FOREIGN KEY (screen_id) REFERENCES screens(id) ON DELETE CASCADE
);
