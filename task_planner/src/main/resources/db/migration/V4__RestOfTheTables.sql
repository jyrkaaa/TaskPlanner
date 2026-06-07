-- ============================================================
--  ISSUES
-- ============================================================

CREATE TABLE issues (
    id          SERIAL PRIMARY KEY,
    tenant_id   INT       NOT NULL,
    code        VARCHAR(64)  NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_issues_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,

    -- code is unique within a tenant, not globally
    CONSTRAINT uq_issues_code_tenant UNIQUE (tenant_id, code)
);

CREATE INDEX idx_issues_tenant_id ON issues (tenant_id);


-- ============================================================
--  TASKS
-- ============================================================

CREATE TABLE tasks (                          -- renamed: singular → plural, consistent
    id          SERIAL PRIMARY KEY,
    issue_id    INT       NOT NULL,
    code        VARCHAR(64),
    description TEXT,                         -- TEXT, not VARCHAR
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_tasks_issue
        FOREIGN KEY (issue_id) REFERENCES issues (id) ON DELETE CASCADE
);

CREATE INDEX idx_tasks_issue_id ON tasks (issue_id);


-- ============================================================
--  TASK RESPONSIBILITY  (lookup, scoped per tenant)
-- ============================================================

CREATE TABLE task_responsibilities (          -- fixed typo + pluralised
    id          SERIAL PRIMARY KEY,
    tenant_id   INT       NOT NULL,
    name        VARCHAR(128) NOT NULL,

    CONSTRAINT fk_tr_tenant
        FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,

    CONSTRAINT uq_tr_name_tenant UNIQUE (tenant_id, name)
);

CREATE INDEX idx_task_responsibilities_tenant_id ON task_responsibilities (tenant_id);


-- ============================================================
--  TASK  ↔  USER  ↔  RESPONSIBILITY  (junction)
-- ============================================================

CREATE TABLE task_users (
    task_id             INT NOT NULL,
    user_id             INT NOT NULL,
    responsibility_id   INT NOT NULL,

    CONSTRAINT pk_task_users PRIMARY KEY (task_id, user_id),

    CONSTRAINT fk_task_users_task
        FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE,
    CONSTRAINT fk_task_users_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_task_users_responsibility
        FOREIGN KEY (responsibility_id) REFERENCES task_responsibilities (id)
);

CREATE INDEX idx_task_users_user_id            ON task_users (user_id);
CREATE INDEX idx_task_users_responsibility_id  ON task_users (responsibility_id);