package xyz.chener.zp.storagev2

import com.alibaba.cloud.sentinel.endpoint.SentinelEndpointAutoConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.transaction.annotation.EnableTransactionManagement
import xyz.chener.zp.common.entity.LoginUserDetails
import xyz.chener.zp.common.utils.ObjectUtilsKt
import xyz.chener.zp.storagev2.config.StorageV2Config


@org.springframework.boot.autoconfigure.SpringBootApplication(exclude = [SentinelEndpointAutoConfiguration::class]
, excludeName = ["org.redisson.spring.starter.RedissonAutoConfiguration"])
@EnableFeignClients
@EnableTransactionManagement
@EnableConfigurationProperties(StorageV2Config::class)
open class Storagev2Application  {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty("csp.sentinel.log.output.type", "console")
            org.springframework.boot.runApplication<Storagev2Application>(*args)
        }
    }

}