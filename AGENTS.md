# Repository Guidance

This file is used for AI tools to better understand our project and development preferences.

## Project

- This is the AAFC DINA Natural History Collection API, built with Spring Boot and Maven.
- Use Java 25, as configured by `pom.xml` and the CI workflow.
- Main Java code is under `src/main/java/ca/gc/aafc/collection`; tests are under the matching `src/test/java/ca/gc/aafc/collection` packages.
- Liquibase changelogs and migrations are under `src/main/resources/db/changelog`.

## Build and Checks

- Run the full CI build and tests with `mvn -B -q verify`.
- Run a focused unit test with `mvn -Dtest=TestClassName test`.
- Run style checks with `mvn -q checkstyle:check` and static analysis with `mvn -q spotbugs:check`.
- Integration tests may require Docker.

## Changes

- Keep changes scoped to the owning API, persistence, configuration, or migration code and follow neighboring patterns.
- Add or update tests in the corresponding test package when behavior changes.
- Do not edit generated output under `target/`; modify the source or configuration that produces it.
- Preserve existing user changes and avoid unrelated refactors.
- UUIDs inserted as database values must be real, generated values. Never hand-write or invent one (e.g. 00000000-0000-0000-0000-000000000001); generate it with a UUID generator such as UUID.randomUUID(), uuidgen, or python -c "import uuid; print(uuid.uuid4())".
- Liquibase changeset IDs should follow the repository's existing naming convention.
- Treat existing Liquibase migration files and changesets as immutable; do not alter them unless the user explicitly asks. Append new migration include entries to the master changelog as needed, but never change or remove its existing content.
