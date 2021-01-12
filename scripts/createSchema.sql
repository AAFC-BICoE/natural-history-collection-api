CREATE SCHEMA collection;

GRANT USAGE ON SCHEMA collection TO $spring_datasource_username;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA collection TO $spring_datasource_username;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA collection TO $spring_datasource_username;

alter default privileges in schema collection grant SELECT, INSERT, UPDATE, DELETE on tables to $spring_datasource_username;
alter default privileges in schema collection grant all on sequences to $spring_datasource_username;
