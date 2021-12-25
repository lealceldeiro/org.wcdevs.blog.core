[Flyway](https://flywaydb.org/documentation/concepts/migrations#versioned-migrations) is used for
managing the DB migrations. Some useful links are:

- [Versioned migrations](https://flywaydb.org/documentation/concepts/migrations#versioned-migrations)
- [Naming](https://flywaydb.org/documentation/concepts/migrations#naming)
- [Spring Boot integration](https://flywaydb.org/documentation/usage/plugins/springboot),
[Execute Flyway Database Migrations on Startup](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway) and [data migration properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.data-migration)
- [Spring](https://flywaydb.org/documentation/concepts/migrations#spring)
- [PostgreSQL](https://flywaydb.org/documentation/database/postgresql)

Currently, the migrations are set up to be used along with the special `{vendor}` placeholder to
use vendor-specific scripts. See [application.yml](https://github.com/lealceldeiro/org.wcdevs.blog.core/blob/main/rest/src/main/resources/application.yml#L28)
