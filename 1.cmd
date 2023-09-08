-javaagent:C:\Users\chen\Desktop\d\skywalking\agent\skywalking-agent\skywalking-agent.jar -Dskywalking.agent.service_name=zp::zp-base-module -Dskywalking.collector.backend_service=127.0.0.1:11800
-javaagent:C:\Users\chen\Desktop\d\skywalking\agent\skywalking-agent\skywalking-agent.jar -Dskywalking.agent.service_name=zp::zp-strong-module -Dskywalking.collector.backend_service=127.0.0.1:11800
-javaagent:C:\Users\chen\Desktop\d\skywalking\agent\skywalking-agent\skywalking-agent.jar -Dskywalking.agent.service_name=zp::zp-gateway-module -Dskywalking.collector.backend_service=127.0.0.1:11800


--add-opens java.base/sun.nio.ch=ALL-UNNAMED
--add-opens java.base/java.lang.invoke=ALL-UNNAMED

