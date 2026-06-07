-- Fix user_role default: was 0, which violated the FK since no role with id=0 exists
ALTER TABLE tenant_users ALTER COLUMN user_role SET DEFAULT 1;
