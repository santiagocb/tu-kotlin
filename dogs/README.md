## Dogbreeds project

### Requirements
- Postgresql installed
- Docker installed

### Run the application
- Run docker command to trigger postgres in local: `docker run --rm -ePOSTGRES_PASSWORD=postgres -p5432:5432 --name my-postgres postgres:13.3`
- Run gradlew bootRun command: `./gradlew bootRun --stacktrace`
