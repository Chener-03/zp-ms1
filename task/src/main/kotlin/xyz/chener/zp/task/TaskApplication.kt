package xyz.chener.zp.task

import com.alibaba.cloud.sentinel.endpoint.SentinelEndpointAutoConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import com.sun.jdi.*
import com.sun.jdi.connect.AttachingConnector
import com.sun.jdi.connect.Connector
import com.sun.jdi.connect.IllegalConnectorArgumentsException
import com.sun.jdi.event.ExceptionEvent
import com.sun.jdi.event.MethodEntryEvent
import com.sun.jdi.event.MethodExitEvent
import org.apache.shardingsphere.elasticjob.api.JobConfiguration
import org.apache.shardingsphere.elasticjob.api.ShardingContext
import org.apache.shardingsphere.elasticjob.lite.api.bootstrap.impl.ScheduleJobBootstrap
import org.apache.shardingsphere.elasticjob.lite.internal.schedule.JobScheduler
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobConfigurationAPI
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.JobOperateAPI
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.operate.JobOperateAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.settings.JobConfigurationAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.JobStatisticsAPIImpl
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.statistics.ShardingStatisticsAPIImpl
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperConfiguration
import org.apache.shardingsphere.elasticjob.reg.zookeeper.ZookeeperRegistryCenter
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob
import org.apache.shardingsphere.elasticjob.tracing.api.TracingConfiguration
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.transaction.annotation.EnableTransactionManagement
import xyz.chener.zp.common.utils.Md5Utiles
import java.io.IOException
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.functions


@org.springframework.boot.autoconfigure.SpringBootApplication(exclude = [SentinelEndpointAutoConfiguration::class]
    , excludeName = ["org.redisson.spring.starter.RedissonAutoConfiguration"])
@EnableFeignClients
@EnableTransactionManagement
open class TaskApplication {

    companion object {



        @JvmStatic
        fun main(args: Array<String>) {
/*

            val requestStr = """
{
    "startTime": "2023-09-20 00:00:00",
    "endTime": "2023-09-20 20:00:00",
    "contractList": [
        "JNCYHT-LRF-0920001"
    ],
    "size": 10,
    "page": 1
}
            """.trimIndent()
            val map: MutableMap<String, Any> = TreeMap()
            map["Kcwl-OpenApi-AppSecret"] = "sdfdsfsdfdsf435436546575b6776878"
            map.putAll(ObjectMapper().readValue(requestStr, TreeMap::class.java) as Map<out String, Any>)

            val sign: String = Md5Utiles.getStrMd5(ObjectMapper().writeValueAsString(map))
            println(System.currentTimeMillis())
            println(sign)
*/
            org.springframework.boot.runApplication<TaskApplication>(*args)


//            ProcessBuilder().redirect


//            ScheduleJobBootstrap(registryCenter,job,jobConfiguration).schedule()

/*
            val hostname = "localhost"

            val port = 12345
            val jmxUrl = "service:jmx:rmi:///jndi/rmi://$hostname:$port/jmxrmi"

            val serviceURL = JMXServiceURL(jmxUrl)
            val connector = JMXConnectorFactory.connect(serviceURL)
            val mbsc = connector.mBeanServerConnection

            val name = ObjectName(JarTaskExecute.REG_PATH)

            val funSignature = getFunSignature(JarTaskMBean::class, JarTaskMBean::handle.name)


            val invoke = mbsc.invoke(name, JarTaskMBean::toOutput.name, null, null)


            val proxy = JMX.newMXBeanProxy(mbsc, name, JarTaskMBean::class.java)
            proxy.handle(TaskData(),1)


            connector.close()*/
        }


        private fun getFunSignature(clazz:KClass<*>, funName:String) : Array<String> {
            val kFunction = clazz.functions.find { it.name == funName } ?: return arrayOf()
            return kFunction.parameters.filter {
                it.kind.name == "VALUE"
            }.map {
                it.type.toString()
            }.toTypedArray()
        }

    }

}