package xyz.chener.zp.storagev2.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "zp.storage.v2")
class StorageV2Config {

    var config : Map<String,String>? = null;

}