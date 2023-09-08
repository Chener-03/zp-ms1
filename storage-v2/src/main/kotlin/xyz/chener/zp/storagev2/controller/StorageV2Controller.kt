package xyz.chener.zp.storagev2.controller

import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryResult
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn
import xyz.chener.zp.common.entity.WriteList
import xyz.chener.zp.common.utils.AssertUrils
import xyz.chener.zp.storagev2.entity.FileSystemMap2
import xyz.chener.zp.storagev2.entity.vo.UploadResult
import xyz.chener.zp.storagev2.service.FileSystemMap2Service


@RestController
@UnifiedReturn
@Slf4j
@RequestMapping(value = ["/api/web/storage-v2","/api/client/storage-v2"])
@Validated
open class StorageV2Controller {


    @Autowired
    lateinit var fileSystemMap2Service: FileSystemMap2Service


    @GetMapping("/getUploadUrl")
    fun getUploadUrl(@RequestParam("path") path:String,@RequestParam(value = "type", required = false)type:String?): UploadResult {
        return fileSystemMap2Service.getUploadUrl(path,type)
    }

    @PostMapping("/confirmUpload")
    fun confirmUpload(@ModelAttribute @Validated uploadResult: UploadResult): UploadResult {
        return fileSystemMap2Service.confirmUpload(uploadResult)
    }

}



@RestController
@RequestMapping(value = ["/api/web/storage-v2/redirect","/api/client/storage-v2/redirect"])
open class StorageV2RedirectController {
    @Autowired
    lateinit var fileSystemMap2Service: FileSystemMap2Service

    @GetMapping("/file")
    @WriteList
    fun getFile(@RequestParam("uid") uid:String,response: HttpServletResponse) {
        val fileUrl = fileSystemMap2Service.getFileUrl(uid)
        if (StringUtils.hasText(fileUrl)){
            response.sendRedirect(fileUrl)
        }else{
            response.status = 404
        }
    }
}
