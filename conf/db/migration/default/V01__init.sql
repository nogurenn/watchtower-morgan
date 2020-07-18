CREATE TABLE covid_observations (
    id                  BIGSERIAL       PRIMARY KEY NOT NULL,
    observation_date    DATE            NOT NULL,
    locality            VARCHAR(255),
    country             VARCHAR(64)     NOT NULL,
    last_update         TIMESTAMP       NOT NULL,
    confirmed           INTEGER         NOT NULL,
    dead                INTEGER         NOT NULL,
    recovered           INTEGER         NOT NULL,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    modified_at         TIMESTAMP WITH TIME ZONE
);


CREATE OR REPLACE FUNCTION update_modified_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.modified_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';
CREATE TRIGGER update_modified BEFORE UPDATE ON covid_observations FOR EACH ROW EXECUTE PROCEDURE update_modified_at_column();
