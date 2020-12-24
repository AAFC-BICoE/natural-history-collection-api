# natural-history-collection-api

AAFC DINA Natural History Collection module implementation.

See DINA Collection module [specifications](https://github.com/DINA-Web/collection-specs).

## Required

* Java 11
* Maven 3.6 (tested)
* Docker 19+ (for running integration tests)

## To Run

For testing purpose a [Docker Compose](https://docs.docker.com/compose/) example file is available in the `local` folder.
Please note that the app will start without Keycloak and in `dev` mode.

Create a new docker-compose.yml file and .env file from the example file in the local directory:

```
cp local/docker-compose.yml.example docker-compose.yml
cp local/*.env .
```

Start the app (default port is 8085):

```
docker-compose up
```

Once the services have started you can access metadata at http://localhost:8085/api/v1/collecting-event

Cleanup:
```
docker-compose down
```
