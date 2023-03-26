FROM openjdk:17-ea-oracle

RUN rm -f /etc/localtime \
&& ln -sv /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
&& echo "Asia/Shanghai" > /etc/timezone

RUN mkdir -p /gatewayapps

COPY ./jarfile/*.jar /gatewayapps
COPY ./skywalking-agent/ /gatewayapps/agent

WORKDIR /gatewayapps

EXPOSE 5001

#gateway-1.0-SNAPSHOT.jar
ENTRYPOINT ["java","-javaagent:/gatewayapps/agent/skywalking-agent.jar","--add-opens","java.base/java.lang.invoke=ALL-UNNAMED","--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","-Dspring.cloud.nacos.discovery.ip=82.156.191.218","-jar","./gateway-1.0-SNAPSHOT.jar"]

