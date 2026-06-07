-- ============================================================
--  ISSUES
-- ============================================================
 
CREATE TABLE issues (
    id          SERIAL PRIMARY KEY,
    tenant_id   BIGINT      NOT NULL,
    code        VARCHAR(64) NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
 
    CONSTRAINT fk_issues_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
 
    -- code unique within a tenant, not globally
    CONSTRAINT uq_issues_tenant_code UNIQUE (tenant_id, code)
);
 
CREATE INDEX idx_issues_tenant_id ON issues (tenant_id);
 
 
-- ============================================================
--  TASK STATUS  (workflow states, scoped per tenant)
-- ============================================================
 
CREATE TABLE task_statuses (
    id          SERIAL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL,
    name        VARCHAR(128) NOT NULL,
    order_nr    INTEGER      NOT NULL,   -- drives UI ordering of columns/states
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
 
    CONSTRAINT fk_task_statuses_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
 
    -- name unique within a tenant
    CONSTRAINT uq_task_statuses_tenant_name  UNIQUE (tenant_id, name),
    -- order unique within a tenant (no two states share the same position)
    CONSTRAINT uq_task_statuses_tenant_order UNIQUE (tenant_id, order_nr),
 
    CONSTRAINT chk_task_statuses_order_nr_positive CHECK (order_nr > 0)
);
 
CREATE INDEX idx_task_statuses_tenant_id ON task_statuses (tenant_id);
 
 
-- ============================================================
--  TASKS
-- ============================================================
 
CREATE TABLE tasks (
    id             SERIAL PRIMARY KEY,
    issue_id       BIGINT      NOT NULL,
    task_status_id BIGINT      NOT NULL,
    code           VARCHAR(64),
    description    TEXT,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
 
    CONSTRAINT fk_tasks_issue
        FOREIGN KEY (issue_id) REFERENCES issues (id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_status
        FOREIGN KEY (task_status_id) REFERENCES task_statuses (id)
);
 
CREATE INDEX idx_tasks_issue_id       ON tasks (issue_id);
CREATE INDEX idx_tasks_task_status_id ON tasks (task_status_id);
 
 
-- ============================================================
--  TASK RESPONSIBILITY  (lookup, scoped per tenant)
-- ============================================================
 
CREATE TABLE task_responsibilities (
    id          SERIAL PRIMARY KEY,
    tenant_id   BIGINT       NOT NULL,
    name        VARCHAR(128) NOT NULL,
 
    CONSTRAINT fk_tr_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
 
    CONSTRAINT uq_tr_tenant_name UNIQUE (tenant_id, name)
);
 
CREATE INDEX idx_task_responsibilities_tenant_id ON task_responsibilities (tenant_id);
 
 
-- ============================================================
--  TASK  ↔  USER  ↔  RESPONSIBILITY  (junction)
-- ============================================================
 
CREATE TABLE task_users (
    task_id             BIGINT NOT NULL,
    user_id             BIGINT NOT NULL,
    responsibility_id   BIGINT NOT NULL,
 
    CONSTRAINT pk_task_users PRIMARY KEY (task_id, user_id),
 
    CONSTRAINT fk_task_users_task
        FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE,
    CONSTRAINT fk_task_users_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_task_users_responsibility
        FOREIGN KEY (responsibility_id) REFERENCES task_responsibilities (id)
);
 
CREATE INDEX idx_task_users_user_id           ON task_users (user_id);
CREATE INDEX idx_task_users_responsibility_id ON task_users (responsibility_id);