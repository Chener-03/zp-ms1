FROM openjdk:17-ea-oracle

RUN rm -f /etc/localtime \
&& ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo "Asia/Shanghai" > /etc/timezone

RUN mkdir -p /storageapps

COPY ./jarfile/*.jar /storageapps
COPY ./skywalking-agent/ /storageapps/agent

WORKDIR /storageapps

EXPOSE 6100

ENTRYPOINT ["java","-javaagent:/storageapps/agent/skywalking-agent.jar","--add-opens","java.base/java.lang.invoke=ALL-UNNAMED","--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","-Dspring.cloud.nacos.discovery.ip=82.156.191.218","-jar","./storage-1.0-SNAPSHOT.jar"]

