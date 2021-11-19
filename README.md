# org.wcdevs.blog.core

[![License: Apache](https://img.shields.io/badge/License-Apache%202.0-blue)](https://opensource.org/licenses/Apache-2.0) [![Java Style Checker](https://img.shields.io/badge/code%20style-checkstyle-blue?style=flat&logo=java&logoColor=f89820)](https://checkstyle.sourceforge.io/) [![Build and Publish](https://github.com/lealceldeiro/org.wcdevs.blog.core/actions/workflows/build-and-publish.yml/badge.svg)](https://github.com/lealceldeiro/org.wcdevs.blog.core/actions/workflows/build-and-publish.yml) [![codecov](https://codecov.io/gh/lealceldeiro/org.wcdevs.blog.core/branch/main/graph/badge.svg?token=CDXSJ1G7GE)](https://codecov.io/gh/lealceldeiro/org.wcdevs.blog.core) [![Core Application Deployment](https://github.com/lealceldeiro/org.wcdevs.blog.awsdeployer/actions/workflows/core-app-deployment.yml/badge.svg)](https://github.com/lealceldeiro/org.wcdevs.blog.awsdeployer/actions/workflows/core-app-deployment.yml)

Holds `org.wcdevs.blog:core`, the core application (API) where all the back-end web-services live in.

## Project structure

- Root (parent) module: `org.wcdevs.blog:core`
- Submodules:
  * `org.wcdevs.blog:common`: contains the common resources across the core application.
  * `org.wcdevs.blog:persistence`: contains all resources holding the logic to communicate with the persistence layer.
  * `org.wcdevs.blog:rest`: contains the exposed webservices.

## Local development

### Requirements

- [Git](https://git-scm.com/)
- [Java](https://jdk.java.net/) 11 (we use [SDKMAN](https://sdkman.io/) for Java versions management)
- [Maven](https://maven.apache.org/index.html)
- [Docker](https://www.docker.com/) (and [Docker Compose](https://docs.docker.com/compose/))
- An IDE (such as [Intellij IDEA](https://www.jetbrains.com/idea/) or Eclipse)

### Build the application image locally
(scripts explained from a Unix environment perspective)

Run:

- `./mvnw clean package`
- `docker build -t org.wcdevs.blog:core .` (*notice the dot (**.**) at the end*)

For more info about docker run `docker --help`.

### Running the whole stack with docker compose

Run `docker-compose up -d`.

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

There are two users created: `admin` (password: `admin`) and john (password `john`).

Also, there's a client registration configuration with initial access token:
```
eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxMmMyMTQ3Yi1iM2NiLTQ3YWUtOTY0Zi1mMGM4MDIzMjZhYTUifQ.eyJleHAiOjE2Njg4NTAwMjYsImlhdCI6MTYzNzMxNDAyNiwianRpIjoiNTIxNDk0ZjMtYzAyOS00ZTE1LWI1OTItYzM2M2JmNjQ0OGFkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDozMDAwL3JlYWxtcy93Y2RldnMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjMwMDAvcmVhbG1zL3djZGV2cyIsInR5cCI6IkluaXRpYWxBY2Nlc3NUb2tlbiJ9.d2_SDmbCAFXtEQPiud5fxSeoBAMh6e0T_veOh8KJncQ
```

And two clients already configured with the following data:

**(1)**

ID: `wcdevs-front-client`

Access type: `public`

Name: `wcDevs front-end application`

Protocol: `openid-connect`

Implicit flow: `enabled`

Root Url: `http://localhost:3000`

Valid redirect URIs: `http://localhost:3000/*`

Web origins: `http://localhost:3000`

**(2)**

ID: `wcdevs-core-client`

Access type: `confidential`

Secret: `857964ff-674c-4892-a31f-50a6df8c319e`

Registration access token:
```
eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxMmMyMTQ3Yi1iM2NiLTQ3YWUtOTY0Zi1mMGM4MDIzMjZhYTUifQ.eyJleHAiOjAsImlhdCI6MTYzNzMxNTExOCwianRpIjoiZmRkNjQyNzItNTMyYi00YjQ0LWE0OTMtOGFhZWE3ODI3ZjQ4IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDozMDAwL3JlYWxtcy93Y2RldnMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjMwMDAvcmVhbG1zL3djZGV2cyIsInR5cCI6IlJlZ2lzdHJhdGlvbkFjY2Vzc1Rva2VuIiwicmVnaXN0cmF0aW9uX2F1dGgiOiJhdXRoZW50aWNhdGVkIn0.PMEaHbANTZ1gTpO9SpeRFV7K00sL3-A1NSqyl6q2AIM
```

Name: `wcDevs core application`

Protocol: `openid-connect`

Direct Access Grants Enabled: `enabled`

Root Url: `http://localhost:8080`

Web origins: `http://localhost:8080`

##### If any of this mocked data needs to be updated do the following, from the root project directory:

- Start a docker container with a keycloak server, by importing the current data
```shell
docker run -d -p 8888:8080 -e KEYCLOAK_USER=keycloak -e KEYCLOAK_PASSWORD=keycloak -e KEYCLOAK_IMPORT=/tmp/wcdevs-realm.json -v $(pwd)/appmocks:/tmp --name kc jboss/keycloak:15.0.2
```
- Access the server admin console by navigating to `http://localhost:8888`
- Login using as a username and password: `keycloak`
- Make the required changes (do not change the real name from `wcdevs`)
- Export the data again by issuing the following in console:
```shell
docker exec -it kc /opt/jboss/keycloak/bin/standalone.sh -Djboss.socket.binding.port-offset=100 -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.realmName=wcdevs -Dkeycloak.migration.usersExportStrategy=REALM_FILE -Dkeycloak.migration.file=/tmp/wcdevs-updated-realm.json
```
- Compare the new data inside `appmocks/wcdevs-updated-realm.json` and `appmocks/wcdevs-realm.json`.
Move the updated data from the updated file to the old file.
