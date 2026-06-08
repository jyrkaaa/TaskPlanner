### TaskManager app (Basically Jira copy)
Full-stack application made to play around with React.js and Spring boot.

### Application Specifics
Application supports users being part of multiple tenants. Tenants can have users and admins. Tenants users can modify own tenants
issues and task. Users who create tenants are automatically admins and all users can ask to join tenants, admins need to accept.
When logging on, users need to choose current tenant to interact with.

### Technology stack
DB: Postgres
FE: React.js | 19.2.6 && Vite 8.0.12
BE: Java | OpenJDK 21.0.2 | Spring Boot	4.0.6
Application is run from one container currently with Docker.
Reverse proxy to be added, nginx probably.
