package xyz.chener.zp.storagev2.service

import com.baomidou.mybatisplus.extension.service.IService
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestParam
import xyz.chener.zp.storagev2.entity.FileSystemMap2
import xyz.chener.zp.storagev2.entity.vo.UploadResult


interface FileSystemMap2Service : IService<FileSystemMap2?> {

    fun getUploadUrl(path:String,type:String?): UploadResult

    fun confirmUpload(uploadResult: UploadResult): UploadResult

    fun getFileUrl(uid:String): String?

}
