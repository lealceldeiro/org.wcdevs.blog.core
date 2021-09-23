# org.wcdevs.blog.core

Holds `org.wcdevs.blog:core`, the core application where all the back-end web-services live in.

- Root (parent) module: `org.wcdevs.blog:core`
- Submodules:
  * `common`: contains the common resources across the core application.
  * `persistence`: contains all resources holding the logic to communicate with the persistence layer.
  * `rest`: contains the exposed webservices.

## Local Development

### Build application image locally

Run:

- `./mvnw clean verify package`
- `docker build -t lealceldeiro/org.wcdevs.blog:core-0.0.1 .` (*notice the dot (**.**) at the end*)

### Docker Compose

Run `docker-compose up -d`.

It can be stopped then using `docker-compose down`.

For more info about docker compose run `docker-compose --help`.
