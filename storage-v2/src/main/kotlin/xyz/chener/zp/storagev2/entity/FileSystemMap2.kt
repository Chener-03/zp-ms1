package xyz.chener.zp.storagev2.entity

import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.extension.activerecord.Model
import lombok.Data
import xyz.chener.zp.common.config.unifiedReturn.annotation.EncryField
import java.util.*


open class FileSystemMap2 : Model<FileSystemMap2?>() {

    @TableId(type = com.baomidou.mybatisplus.annotation.IdType.AUTO)
    var id: Long? = null

    //资源ID 包含路径
    var resourceUuid: String? = null

    @EncryField
    var fileName: String? = null

    var createTime: Date? = null

    var uploadUserId: Long? = null

    //七牛 or  minio
    var storageType: String? = null

    //权限  1 | 2 | 4 | 8 .......
    var fileRwType: Int? = null


}


