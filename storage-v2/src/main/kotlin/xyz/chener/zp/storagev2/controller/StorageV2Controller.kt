package xyz.chener.zp.storagev2.controller

import lombok.extern.slf4j.Slf4j
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryResult
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn
import xyz.chener.zp.common.entity.WriteList
import xyz.chener.zp.storagev2.entity.FileSystemMap2


@RestController
@UnifiedReturn
@Slf4j
@RequestMapping(value = ["/api/web/storage-v2","/api/client/storage-v2"])
@Validated
class StorageV2Controller {


    @GetMapping("/test")
    @WriteList
    @EncryResult
    fun test():FileSystemMap2{
        val r = FileSystemMap2()
        r.fileName = "test"
        r.id = 123;
        return r;
    }


}