CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS budget_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    amount NUMERIC(14, 2) NOT NULL,
    entry_type VARCHAR(16) NOT NULL CHECK (entry_type IN ('INCOME', 'EXPENSE')),
    category VARCHAR(128),
    entry_date DATE NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_budget_entries_entry_date
    ON budget_entries (entry_date);

CREATE INDEX IF NOT EXISTS idx_budget_entries_entry_type
    ON budget_entries (entry_type);
