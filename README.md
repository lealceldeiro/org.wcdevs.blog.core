# com.wcdevs.blog.core

Holds `com.wcdevs.blog.core`, the core application where all the back-end web-services live in.

- Root (parent) module: `com.wcdevs.blog.core:core`
- Submodules:
  * `common`: contains the common resources across the core application.
  * `persistence`: contains all resources holding the logic to communicate with the persistence layer.
  * `rest`: contains the exposed webservices.

## Local Development

### Docker Compose

From the application root directory, start the app using `docker-compose up -d`.

It can be stopped then using `docker-compose down`.

For more info about docker compose run `docker-compose --help`.
