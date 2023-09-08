FROM openjdk:17-ea-oracle

RUN rm -f /etc/localtime \
&& ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo "Asia/Shanghai" > /etc/timezone

RUN mkdir -p /userapps

COPY ./jarfile/*.jar /userapps
COPY ./skywalking-agent/ /userapps/agent

WORKDIR /userapps

EXPOSE 6050


ENTRYPOINT ["java","-javaagent:/userapps/agent/skywalking-agent.jar","--add-opens","java.base/java.lang.invoke=ALL-UNNAMED","--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","-Dspring.cloud.nacos.discovery.ip=82.156.191.218","-jar","./user-1.0-SNAPSHOT.jar"]

