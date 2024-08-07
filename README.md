# natural-history-collection-api

AAFC DINA Natural History Collection module implementation.

Features :
 * Implements DINA collection [specification](https://dina-web.github.io/collection-specs/)
 * Aligns with [Darwin Core](https://dwc.tdwg.org/terms/) standard
 * Supports field extensions based on [MixS](https://github.com/GenomicsStandardsConsortium/mixs) specification
 * Optional Message Queue producer for integration (e.g. search index)

## Container Image
The Docker Image is available on [DockerHub](https://hub.docker.com/r/aafcbicoe/natural-history-collection-api/tags).

## Documentation
See [documentation](https://aafc-bicoe.github.io/natural-history-collection-api/) page.

## Required

* Java 21
* Maven 3.8 (tested)
* Docker 20+ (for running integration tests)

## dina-base-api

`natural-history-collection-api` relies heavily on [dina-base-api](https://aafc-bicoe.github.io/dina-base-api/).

## IDE

`natural-history-collection-api` requires [Project Lombok](https://projectlombok.org/) to be setup in your IDE.

See Lombok [Setup documentation](https://projectlombok.org/setup/overview)

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
