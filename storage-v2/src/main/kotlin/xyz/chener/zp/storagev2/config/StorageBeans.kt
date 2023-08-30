package xyz.chener.zp.storagev2.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.context.config.annotation.RefreshScope
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.chener.zp.storagev2.core.impl.MINIOFile


@Configuration
class StorageBeans {


    @Autowired
    lateinit var storageV2Config: StorageV2Config

    @Bean("xyz.chener.zp.storagev2.core.impl.MINIOFile")
    @RefreshScope
    fun miniofile(): MINIOFile {
        return MINIOFile(storageV2Config)
    }

}