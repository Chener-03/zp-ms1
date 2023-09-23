package xyz.chener.zp.storagev2


import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.transaction.annotation.EnableTransactionManagement
import xyz.chener.zp.storagev2.config.StorageV2Config


@org.springframework.boot.autoconfigure.SpringBootApplication(excludeName = ["org.redisson.spring.starter.RedissonAutoConfiguration"])
@EnableFeignClients
@EnableTransactionManagement
@EnableConfigurationProperties(StorageV2Config::class)
open class Storagev2Application  {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            org.springframework.boot.runApplication<Storagev2Application>(*args)
        }
    }

}