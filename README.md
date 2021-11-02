# org.wcdevs.blog:core

[![License: Apache](https://img.shields.io/badge/License-Apache%202.0-blue)](https://opensource.org/licenses/Apache-2.0) [![Build and Publish](https://github.com/lealceldeiro/org.wcdevs.blog.core/actions/workflows/build-and-publish.yml/badge.svg)](https://github.com/lealceldeiro/org.wcdevs.blog.core/actions/workflows/build-and-publish.yml)

Holds `org.wcdevs.blog:core`, the core application where all the back-end web-services live in.

- Root (parent) module: `org.wcdevs.blog:core`
- Submodules:
  * `org.wcdevs.blog:common`: contains the common resources across the core application.
  * `org.wcdevs.blog:persistence`: contains all resources holding the logic to communicate with the persistence layer.
  * `org.wcdevs.blog:rest`: contains the exposed webservices.

## Local Development

### Build application image locally

Run:

- `./mvnw clean verify package`
- `docker build -t org.wcdevs.blog:core .` (*notice the dot (**.**) at the end*)

For more info about docker run `docker --help`.

### Docker Compose

Run `docker-compose up -d`.

It can be stopped then using `docker-compose down`.

For more info about docker compose run `docker-compose --help`.
