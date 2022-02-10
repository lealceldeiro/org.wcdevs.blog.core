### Local keycloak server
A keycloak server is configured to start from the docker-compose file. After this has started, the
administration console can be accessed by navigating to `http://localhost:8888` and login using as
a username and password `keycloak`.

There are the following users created:

- username: `admin@wcdevs.org`, password: `admin`, roles: `USER`, `ADMIN`
- username: `john@wcdevs.org`, password `john`,  roles: `USER`, `AUTHOR`
- username: `edi@wcdevs.org`, password `edi`,  roles: `USER`, `EDITOR`
- username: `susan@wcdevs.org`, password `susan`,  role: `USER`

Also, there's a client registration configuration with initial access token:
```
eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICIxMmMyMTQ3Yi1iM2NiLTQ3YWUtOTY0Zi1mMGM4MDIzMjZhYTUifQ.eyJleHAiOjE2Njg4NTAwMjYsImlhdCI6MTYzNzMxNDAyNiwianRpIjoiNTIxNDk0ZjMtYzAyOS00ZTE1LWI1OTItYzM2M2JmNjQ0OGFkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDozMDAwL3JlYWxtcy93Y2RldnMiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjMwMDAvcmVhbG1zL3djZGV2cyIsInR5cCI6IkluaXRpYWxBY2Nlc3NUb2tlbiJ9.d2_SDmbCAFXtEQPiud5fxSeoBAMh6e0T_veOh8KJncQ
```

And three clients already configured with the following data:

#### Front-end client

- Client ID: `wcdevs-front-client`
- Access type: `confidential`
- Name: `wcDevs front-end application`
- Client Protocol: `openid-connect`
- Standard Flow: `enabled`
- Secret: `3d79519d-639f-4c2f-8137-2f5683d85cc9`
- Issuer URL: `http://localhost:8888/auth/realms/wcdevs` (or `http://keycloak.service:8888/auth/realms/wcdevs` if the **runtime** mock data is loaded)
- Root Url: `http://localhost:8888` (or `http://keycloak.service:8888` if the **runtime** mock data is loaded)
- Valid redirect URIs: `*`
- Web origins: `*`

#### Core (this) app

- Client ID: `wcdevs-core-client`
- Access type: `confidential`
- Name: `wcDevs core application`
- Client Protocol: `openid-connect`
- Direct Access Grants Enabled: `enabled`
- Secret: `857964ff-674c-4892-a31f-50a6df8c319e`
- Issuer URL: `http://localhost:8888/auth/realms/wcdevs` (or `http://keycloak.service:8888/auth/realms/wcdevs` if the **runtime** mock data is loaded)
- Root Url: `http://localhost:8888` (or `http://keycloak.service:8888` if the **runtime** mock data is loaded)
- Web origins: `*`

#### Public client

- Client ID: `public-client`
- Access type: `public`
- Name: `wcDevs public client`
- Description: `A public client for getting tokens directly from keycloak`
- Client Protocol: `openid-connect`
- Implicit Flow Enabled: `enabled`
- Issuer URL: `http://localhost:8888/auth/realms/wcdevs` (or `http://keycloak.service:8888/auth/realms/wcdevs` if the **runtime** mock data is loaded)
- Root Url: `http://localhost:8888` (or `http://keycloak.service:8888` if the **runtime** mock data is loaded)
- Valid redirect URIs: `*`
- Web origins: `*`

##### If any of this mocked data needs to be updated do the following, from the root project directory:

1. Start a docker container with a keycloak server, by importing the current data.
```shell
docker run -d -p 8888:8080 -e KEYCLOAK_USER=keycloak -e KEYCLOAK_PASSWORD=keycloak -e KEYCLOAK_IMPORT=/tmp/wcdevs-realm.json -v $(pwd)/appmocks:/tmp --name kc jboss/keycloak:15.0.2
```
2. Access the server admin console by navigating to `http://localhost:8888`.
3. Login using as a username and password: `keycloak`.
4. Make the required changes (do not change the real name from `wcdevs`).
5. Export the data again by issuing the following in console:
```shell
docker exec -it kc /opt/jboss/keycloak/bin/standalone.sh -Djboss.socket.binding.port-offset=100 -Dkeycloak.migration.action=export -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.realmName=wcdevs -Dkeycloak.migration.usersExportStrategy=REALM_FILE -Dkeycloak.migration.file=/tmp/wcdevs-updated-realm.json
```
6. Kill the process once the export is finished (optional, if the command was issues in the same
console, or it will be killed in step 8)
7. Compare the new data inside `appmocks/wcdevs-updated-realm.json` and `appmocks/wcdevs-realm.json`.
 Move the updated data from the updated file to the old file. (Optionally, the file
`appmocks/runtime-wcdevs-realm.json` should also be updated).
8. Once finished, you can stop the keycloak server using `docker stop kc` (`kc` should be visible if
`docker ps -a` is issued).
9. Then it can be removed by using `docker rm kc` (and it should not be visible when `docker ps -a`
is issued).
