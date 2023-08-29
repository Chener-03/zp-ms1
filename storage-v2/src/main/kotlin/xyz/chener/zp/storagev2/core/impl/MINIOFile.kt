package xyz.chener.zp.storagev2.core.impl

import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.metrics.ApplicationStartup
import org.springframework.stereotype.Service
import xyz.chener.zp.storagev2.core.FileInterface
import java.lang.Exception


@Service("xyz.chener.zp.storagev2.core.impl.MINIOFile")
class MINIOFile : FileInterface ,ApplicationListener<ApplicationStartedEvent> {

    companion object {
        val log : Logger = org.slf4j.LoggerFactory.getLogger(MINIOFile::class.java)
    }

    val TYPE = "MINIO"

    lateinit var minioClient:MinioClient

    @Autowired
    lateinit var storageV2Config: xyz.chener.zp.storagev2.config.StorageV2Config

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        //mKnPWIhs4POXV2WdBweC       6y7PVhZnX9XzjBiM3NZEcBDVOBBUvKVmdfrIvdYv
        minioClient = MinioClient.builder().also {
            it.endpoint("http://101.42.12.133:29001")
            it.credentials("mKnPWIhs4POXV2WdBweC", "6y7PVhZnX9XzjBiM3NZEcBDVOBBUvKVmdfrIvdYv")
        }.build()?.also {
            log.info("minio client init success")
        }!!

        val presignedObjectUrl = minioClient.getPresignedObjectUrl(
            GetPresignedObjectUrlArgs.builder()
                .bucket("zp-admin-dev")
                .`object`("sky.yml").build()
        )

        println(presignedObjectUrl)

    }

    override fun save(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getUrl(): String {
        TODO("Not yet implemented")
    }
}