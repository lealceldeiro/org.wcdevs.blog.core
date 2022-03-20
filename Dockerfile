# general ref: https://docs.spring.io/spring-boot/docs/2.6.4/reference/htmlsingle/#container-images
FROM openjdk:11-jdk-slim as BUILD_STAGE

ARG app_jar_name="org.wcdevs.blog-rest"

WORKDIR /workspace

# ref: https://spring.io/guides/topicals/spring-boot-docker
COPY mvnw .
COPY .mvn .mvn

COPY pom.xml .
COPY persistence/pom.xml ./persistence/pom.xml
COPY common/pom.xml ./common/pom.xml
COPY rest/pom.xml ./rest/pom.xml

RUN ./mvnw -B -f pom.xml dependency:go-offline

COPY persistence/src ./persistence/src/
COPY common/src ./common/src/
COPY rest/src ./rest/src/

RUN ./mvnw -B clean package
# ref: http://www.gnu.org/software/coreutils/mkdir
RUN mkdir -p dependencies && (cd dependencies; jar -xf ../rest/target/${app_jar_name}.jar)

FROM openjdk:11-jre-slim as RUN_STAGE
LABEL author="Asiel Leal Celdeiro"

ARG app_port=8080
ARG management_port=8082

EXPOSE ${app_port}
EXPOSE ${management_port}

RUN mkdir -p "/wcd_app"

# ref: https://spring.io/guides/gs/spring-boot-docker/
RUN adduser --system --group wcdevs
RUN chown -R wcdevs /wcd_app
USER wcdevs:wcdevs

COPY --from=BUILD_STAGE /workspace/dependencies/BOOT-INF/lib /wcd_app/lib
COPY --from=BUILD_STAGE /workspace/dependencies/META-INF /wcd_app/META-INF
COPY --from=BUILD_STAGE /workspace/dependencies/BOOT-INF/classes /wcd_app

ENTRYPOINT ["java", "-cp", "wcd_app:wcd_app/lib/*", "org.wcdevs.blog.core.rest.Application"]
