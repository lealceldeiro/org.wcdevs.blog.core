FROM maven:3.8.2-openjdk-11 as BUILD_STAGE

COPY persistence/src ./persistence/src/
COPY persistence/pom.xml ./persistence/pom.xml

COPY common/src ./common/src/
COPY common/pom.xml ./common/pom.xml

COPY rest/src ./rest/src/
COPY rest/pom.xml ./rest/pom.xml

COPY pom.xml .

RUN mvn -B -f pom.xml dependency:go-offline
RUN mvn -B clean package

FROM openjdk:11-jre-slim as RUN_STAGE

ARG app_jar_name_arg="org.wcdevs.blog-rest"
ARG app_port_arg=8080
ARG management_port_arg=8082

ENV APP_JAR=${app_jar_name_arg}.jar
ENV APP_PORT=${app_port_arg}
ENV APP_MANAGEMENT_PORT=${management_port_arg}

LABEL author="Asiel Leal Celdeiro"
EXPOSE ${APP_PORT}
EXPOSE ${APP_MANAGEMENT_PORT}

RUN mkdir -p "/wcd_app"

# https://spring.io/guides/gs/spring-boot-docker/
RUN adduser --system --group wcdevs
RUN chown -R wcdevs /wcd_app
USER wcdevs:wcdevs

COPY --from=BUILD_STAGE ./rest/target/${APP_JAR} /wcd_app/${APP_JAR}

ENTRYPOINT java -jar /wcd_app/${APP_JAR}
