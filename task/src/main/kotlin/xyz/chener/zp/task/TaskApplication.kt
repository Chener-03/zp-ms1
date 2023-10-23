package xyz.chener.zp.task


import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.shardingsphere.elasticjob.infra.listener.ElasticJobListener
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.transaction.annotation.EnableTransactionManagement
import xyz.chener.zp.common.utils.Md5Utiles
import xyz.chener.zp.task.config.TaskConfiguration
import xyz.chener.zp.task.entity.NOTIFY_TIME
import xyz.chener.zp.task.entity.NOTIFY_TYPE
import xyz.chener.zp.task.entity.TaskMetadata
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.functions


@org.springframework.boot.autoconfigure.SpringBootApplication//(excludeName = ["org.redisson.spring.starter.RedissonAutoConfiguration"])
@EnableFeignClients
@EnableTransactionManagement
@EnableConfigurationProperties(TaskConfiguration::class)
open class TaskApplication {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {



                        val requestStr = """
                            {
                                "startTime": "2023-09-12 10:00:00",
                                "endTime": "2023-09-12 11:00:00"
                            }
                        """.trimIndent()
                        val map: MutableMap<String, Any> = TreeMap()
                        map["Kcwl-OpenApi-AppSecret"] = "misydj7fd27b04649892df521079s5g58"
                        map.putAll(ObjectMapper().readValue(requestStr, TreeMap::class.java) as Map<out String, Any>)

                        val sign: String = Md5Utiles.getStrMd5(ObjectMapper().writeValueAsString(map))
                        println(System.currentTimeMillis())
                        println(sign)


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