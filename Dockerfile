FROM openjdk:11-jre-slim

ARG app_jar_name="org.wcdevs.blog-rest"
ARG app_version="0.0.1"
ARG app_port=8080
ARG management_port=8082

ENV JAR_TO_RUN=${app_jar_name}-${app_version}.jar
ENV CORE_APP_LISTEN_PORT=${app_port}
ENV CORE_APP_MANAGEMENT_PORT=${management_port}

LABEL author="Asiel Leal Celdeiro"
LABEL version=${app_version}
EXPOSE ${CORE_APP_LISTEN_PORT}
EXPOSE ${CORE_APP_MANAGEMENT_PORT}

RUN mkdir -p "/wcd_app"

# https://spring.io/guides/gs/spring-boot-docker/
RUN adduser --system --group wcdevs
RUN chown -R wcdevs /wcd_app
USER wcdevs:wcdevs

ARG target=rest/target
COPY ${target}/${JAR_TO_RUN} /wcd_app/${JAR_TO_RUN}
ENTRYPOINT java -jar /wcd_app/${JAR_TO_RUN}
