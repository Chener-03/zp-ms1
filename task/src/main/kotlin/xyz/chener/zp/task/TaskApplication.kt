package xyz.chener.zp.task


import com.alibaba.nacos.api.NacosFactory
import com.alibaba.nacos.api.naming.pojo.Instance
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.transaction.annotation.EnableTransactionManagement
import xyz.chener.zp.common.config.feign.loadbalance.NormalLoadBalanceAutoConfiguration
import xyz.chener.zp.common.utils.Md5Utiles
import xyz.chener.zp.task.config.TaskConfiguration
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.*


@org.springframework.boot.autoconfigure.SpringBootApplication
@EnableFeignClients
@EnableTransactionManagement
@EnableConfigurationProperties(TaskConfiguration::class)
/*@LoadBalancerClients(
    LoadBalancerClient(
        name = "zp-base-module",
        configuration = [NormalLoadBalanceAutoConfiguration::class]
    )
)*/
open class TaskApplication {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            val properties = Properties()
            properties["serverAddr"] = "101.42.12.133:8848"
            properties["namespace"] = "1379388b-aed1-4082-8ed2-54699a4cc9d4"
            properties["group"] = "zp"
            properties["username"] = "nacos"
            properties["password"] = "nacos123456"


//            val namingService = NacosFactory.createNamingService(properties)

//            namingService.registerInstance("fuck","zp", Instance().also {
//                it.ip = "1.1.1.2"
//                it.clusterName = "fuckname"
//                it.port = 1299
//                it.metadata["label"] = "test"
//            })

//            val allInstances = namingService.getAllInstances("zp-base-module","zp")
//            namingService.selectOneHealthyInstance()

//            Thread.sleep(10000010);

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


            SpringApplication.run(TaskApplication::class.java, *args)

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

    }

}