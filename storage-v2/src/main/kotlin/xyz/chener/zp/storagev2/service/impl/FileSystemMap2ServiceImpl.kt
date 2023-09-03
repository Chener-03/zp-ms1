package xyz.chener.zp.storagev2.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import xyz.chener.zp.common.entity.LoginUserDetails
import xyz.chener.zp.common.utils.AssertUrils
import xyz.chener.zp.common.utils.ObjectUtils
import xyz.chener.zp.storagev2.core.FileInterface
import xyz.chener.zp.storagev2.core.FileStorageTypeEnum
import xyz.chener.zp.storagev2.dao.FileSystemMap2Dao
import xyz.chener.zp.storagev2.entity.FileAuthEnum
import xyz.chener.zp.storagev2.entity.FileSystemMap2
import xyz.chener.zp.storagev2.entity.vo.UploadResult
import xyz.chener.zp.storagev2.error.UploadError
import xyz.chener.zp.storagev2.error.UploadPathNotBeNull
import xyz.chener.zp.storagev2.service.FileSystemMap2Service
import java.util.Date
import java.util.UUID
import kotlin.reflect.KClass


@Service
open class FileSystemMap2ServiceImpl : ServiceImpl<FileSystemMap2Dao,   FileSystemMap2>(), FileSystemMap2Service{
    override fun getUploadUrl(path: String,type:String?): UploadResult {
        AssertUrils.state(StringUtils.hasText(path), UploadPathNotBeNull::class.java)
        val storageType = if (StringUtils.hasText(type)) type else FileStorageTypeEnum.MINIO.type

        val fileInterface = FileInterface.get(storageType!!)
        val fileuuid = UUID.randomUUID().toString()

        val saveUrl = fileInterface?.saveUrl(fileuuid)
        AssertUrils.state(saveUrl != null, UploadError::class.java)

        return UploadResult().also {
            it.fileUID = fileuuid
            it.url = saveUrl
            it.success = true
            it.storageType = storageType
            it.path = path
        }
    }


    override fun confirmUpload(uploadResult: UploadResult): UploadResult {
        val fileSystemMap2 = FileSystemMap2().also {
            it.fileName = uploadResult.path
            it.fileRwType = FileAuthEnum.default()
            it.storageType = uploadResult.storageType
            it.createTime = Date()
            it.resourceUuid = uploadResult.fileUID
            val loginUserDetails = SecurityContextHolder.getContext().authentication?.details as? LoginUserDetails
            it.uploadUserId = loginUserDetails?.userId
        }

        val fm = this.ktQuery().eq(FileSystemMap2::fileName, fileSystemMap2.fileName)
            .eq(FileSystemMap2::uploadUserId, fileSystemMap2.uploadUserId).one()
        if (fm == null){
            this.save(fileSystemMap2)
        }else{
            fileSystemMap2.id = fm.id
            this.updateById(fileSystemMap2)
        }

        return uploadResult.also {
            it.success = true
        }
    }



    override fun getFileUrl(uid: String): String? {
        this.ktQuery().eq(FileSystemMap2::resourceUuid, uid).one()?.let {
            if (FileAuthEnum.contain(it.fileRwType?:0, FileAuthEnum.PUBLIC_READ)){
                val fileInterface = FileInterface.get(it.storageType!!)
                return@getFileUrl fileInterface?.getUrl(uid)
            }
        }
        return null
    }

}