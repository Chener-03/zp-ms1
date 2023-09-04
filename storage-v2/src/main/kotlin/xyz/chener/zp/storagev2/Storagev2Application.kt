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

            var b2 = File("C:\\Users\\chen\\Desktop\\d\\ZP\\ZP\\zp-ms1\\storage-v2\\09-04-2023-14-20-15_files_list\\OIP.jpg").asRequestBody("application/octet-stream".toMediaTypeOrNull())

            val requestBody: RequestBody = object : RequestBody() {
                override fun contentType(): MediaType? {
                    return "application/octet-stream".toMediaTypeOrNull()
                }

                @Throws(IOException::class)
                override fun writeTo(sink: BufferedSink) {

                    val outputStream: OutputStream = sink.outputStream()
                    outputStream.write(Files.readAllBytes(Path.of("C:\\Users\\chen\\Desktop\\d\\ZP\\ZP\\zp-ms1\\storage-v2\\09-04-2023-14-20-15_files_list\\OIP.jpg")))

                    outputStream.flush()
                    outputStream.close()
                }
            }

            val execute = OkHttpClient().newCall(
                Request.Builder().method("PUT", b2)
                    .url("http://101.42.12.133:29001/zp-admin-dev/cnm.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=mKnPWIhs4POXV2WdBweC%2F20230904%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20230904T065736Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=20a59199f160662b7bcd5e9b5f64da627ee2ea457beb279eb40b22bef07dc386")
                    .build()
            ).execute()

            System.setProperty("csp.sentinel.log.output.type", "console")
            org.springframework.boot.runApplication<Storagev2Application>(*args)
        }
    }

}