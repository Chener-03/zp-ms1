package xyz.chener.zp.storagev2.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "zp.storage.v2")
class StorageV2Config {

    var config : Map<String,String>? = null;

    // 下载链接过期时间（秒）
    var downloadUrlExp : Int = 60

    // 上传文件链接过期时间（秒）
    var uploadUrlExp : Int = 60 * 10

}