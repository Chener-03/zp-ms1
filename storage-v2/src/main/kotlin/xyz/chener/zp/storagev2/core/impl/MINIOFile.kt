package xyz.chener.zp.storagev2.core.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.minio.*
import io.minio.http.Method
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import xyz.chener.zp.common.utils.AssertUrils
import xyz.chener.zp.storagev2.core.FileInterface
import xyz.chener.zp.storagev2.error.MinioClientNotStartError
import java.io.ByteArrayInputStream
import java.util.concurrent.TimeUnit


open class MINIOFile (val storageV2Config: xyz.chener.zp.storagev2.config.StorageV2Config) : FileInterface{

    companion object {
        val log : Logger = org.slf4j.LoggerFactory.getLogger(MINIOFile::class.java)
    }

    val TYPE = "MINIO"

    var minioClient:MinioClient?=null

    var minioConfig:MinioConfig?=null


    init {
        storageV2Config.config?.get(TYPE)?.run {
            minioConfig = ObjectMapper().readValue(this, object : TypeReference<MinioConfig>() {})
            minioClient = MinioClient.builder().also {
                it.endpoint(minioConfig?.endpoint)
                it.credentials(minioConfig?.ak, minioConfig?.sk)
            }.build()?.also {
                log.info("minio client init success")
            }
        }
    }



    override fun save(data:ByteArray,path:String): Boolean {
        AssertUrils.state(minioClient!=null,MinioClientNotStartError::class.java)

        try {
            PutObjectArgs.builder().bucket(minioConfig!!.bucketName).`object`(path).stream(ByteArrayInputStream(data),
                data.size.toLong(),-1).build().run {
                minioClient!!.putObject(this)
            }
        }catch (e:Exception){
            log.error("minio save file error",e)
            return false
        }
        return true
    }

    override fun getUrl(file:String): String? {
        AssertUrils.state(minioClient!=null,MinioClientNotStartError::class.java)

        val url = GetPresignedObjectUrlArgs.builder().expiry(storageV2Config.downloadUrlExp,TimeUnit.SECONDS)
            .bucket(minioConfig?.bucketName).`object`(file).method(Method.GET).build()
        return minioClient?.getPresignedObjectUrl(url)
    }

    override fun saveUrl(file: String): String? {
        AssertUrils.state(minioClient!=null,MinioClientNotStartError::class.java)

        var url = GetPresignedObjectUrlArgs.builder().expiry(storageV2Config.uploadUrlExp,TimeUnit.SECONDS)
            .bucket(minioConfig?.bucketName).`object`(file).method(Method.PUT).build()
        url = GetPresignedObjectUrlArgs.builder().expiry(7,TimeUnit.DAYS)
            .bucket(minioConfig?.bucketName).`object`(file).method(Method.PUT).build()
        return minioClient?.getPresignedObjectUrl(url)
    }

    override fun delete(file: String): Boolean {
        AssertUrils.state(minioClient!=null,MinioClientNotStartError::class.java)

        RemoveObjectArgs.builder().bucket(minioConfig!!.bucketName).`object`(file).build().runCatching {
            minioClient!!.removeObject(this)
            return true
        }.onFailure {
            log.error("minio delete file error",it)
        }
        return false
    }

    override fun exist(file: String): Boolean {
        AssertUrils.state(minioClient!=null,MinioClientNotStartError::class.java)

        minioClient?.statObject(StatObjectArgs.builder().bucket(minioConfig!!.bucketName).`object`(file).build())?.run {
            return@exist true
        }
        return false
    }
}


data class MinioConfig(val ak:String,val sk:String,val endpoint:String,val bucketName:String){
    constructor():this("","","","")
}




