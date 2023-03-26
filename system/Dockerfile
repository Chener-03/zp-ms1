FROM openjdk:17-ea-oracle

RUN rm -f /etc/localtime \
&& ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo "Asia/Shanghai" > /etc/timezone

RUN mkdir -p /systemapps

COPY ./jarfile/*.jar /systemapps
COPY ./skywalking-agent/ /systemapps/agent

WORKDIR /systemapps

EXPOSE 6168

ENTRYPOINT ["java","-javaagent:/systemapps/agent/skywalking-agent.jar","--add-opens","java.base/java.lang.invoke=ALL-UNNAMED","--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","-Dspring.cloud.nacos.discovery.ip=82.156.191.218","-jar","./system-1.0-SNAPSHOT.jar"]

