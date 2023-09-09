FROM openjdk:17-ea-oracle

RUN rm -f /etc/localtime \
&& ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo "Asia/Shanghai" > /etc/timezone

RUN mkdir -p /apps

COPY ./jarfile/*.jar /apps
COPY ./skywalking-agent/ /apps/agent

WORKDIR /apps

EXPOSE 26308

# storage-v2-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-javaagent:/apps/agent/skywalking-agent.jar","--add-opens","java.base/java.lang.invoke=ALL-UNNAMED","--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","-Dspring.cloud.nacos.discovery.ip=82.156.191.218","-jar","./storage-v2-1.0-SNAPSHOT.jar"]

