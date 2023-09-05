package xyz.chener.zp.storagev2

import com.alibaba.cloud.sentinel.endpoint.SentinelEndpointAutoConfiguration
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.BufferedSink
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.transaction.annotation.EnableTransactionManagement
import xyz.chener.zp.storagev2.config.StorageV2Config
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.Path


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