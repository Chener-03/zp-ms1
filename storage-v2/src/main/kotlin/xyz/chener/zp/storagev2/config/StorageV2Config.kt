package xyz.chener.zp.storagev2.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cloud.context.config.annotation.RefreshScope


@ConfigurationProperties(prefix = "zp.storage.v2")
@RefreshScope
class StorageV2Config {

    var config : Map<String,String>? = null;

    // 下载链接过期时间（秒）
    var downloadUrlExp : Int = 60

    // 上传文件链接过期时间（秒）
    var uploadUrlExp : Int = 60 * 10

}