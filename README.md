# org.wcdevs.blog.core

[![License: Apache](https://img.shields.io/badge/License-Apache%202.0-blue)](https://opensource.org/licenses/Apache-2.0) [![Java Style Checker](https://img.shields.io/badge/code%20style-checkstyle-blue?style=flat&logo=java&logoColor=f89820)](https://checkstyle.sourceforge.io/) [![Build and Publish](https://github.com/lealceldeiro/org.wcdevs.blog.core/actions/workflows/build-and-publish.yml/badge.svg)](https://github.com/lealceldeiro/org.wcdevs.blog.core/actions/workflows/build-and-publish.yml) [![codecov](https://codecov.io/gh/lealceldeiro/org.wcdevs.blog.core/branch/main/graph/badge.svg?token=CDXSJ1G7GE)](https://codecov.io/gh/lealceldeiro/org.wcdevs.blog.core) [![Core Application Deployment](https://github.com/lealceldeiro/org.wcdevs.blog.awsdeployer/actions/workflows/core-app-deployment.yml/badge.svg)](https://github.com/lealceldeiro/org.wcdevs.blog.awsdeployer/actions/workflows/core-app-deployment.yml)

Holds `org.wcdevs.blog:core`, the core application (API) where all the back-end web-services live in.

## Contributing

Before start contributing to this project be sure you're familiar with our
[Contribution guidelines](CONTRIBUTING.md) and our [Code of Conduct](CODE_OF_CONDUCT.md).

## Project structure

- Root (parent) module: `org.wcdevs.blog:core`
- Submodules:
  * `org.wcdevs.blog:common`: contains the common resources across the core application.
  * `org.wcdevs.blog:persistence`: contains all resources holding the logic to communicate with the persistence layer.
  * `org.wcdevs.blog:rest`: contains the exposed webservices.

## Local setup

### Requirements

#### Development
- [Git](https://git-scm.com/)
- [Java](https://jdk.java.net/) 11 (we use [SDKMAN](https://sdkman.io/) for Java versions management)
- [Maven](https://maven.apache.org/index.html) (alternatively you can use the Maven wrapper by
replacing all usages of the command `mvn` by `./mvnw`)
- [Docker](https://www.docker.com/) (and [Docker Compose](https://docs.docker.com/compose/))
- An IDE (such as [Intellij IDEA](https://www.jetbrains.com/idea/) or Eclipse)

#### Only running the API
- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com/) (and [Docker Compose](https://docs.docker.com/compose/))

### Debugging the application

- Run `docker-compose up -d wcdevs_db keycloak` to start the database and the keycloak server
- Run the Spring Boot application using your favorite IDE (use the `local` profile)

### Running the API with docker compose

- Run `docker-compose up -d wcdevs_db keycloak.rt wcdevs_app`

It can be stopped then using `docker-compose down`.

For more info about docker compose run `docker-compose --help`.

#### Health check

To check the application is running you can hit this endpoint, which should return
`{"status": "UP"}`: `<base_url>/manage/health`

Example request:
```http request
GET /manage/health HTTP/1.1
Host: localhost:82
```

Example response:
```json
{
  "status": "UP"
}
```

#### API Docs

The API documentation can be seen once the app is up and running by navigating to
`<base_url>/docs/index.html`. Example:
```
http://localhost/docs/index.html
```

#### Keycloak

To access the keycloak admin console navigate to `http://localhost:8888` and login using as a username and password `keycloak`.

For more info see the [keycloak docs](./KEYCLOAK.md)
