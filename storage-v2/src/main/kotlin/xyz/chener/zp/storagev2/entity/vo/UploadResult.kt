package xyz.chener.zp.storagev2.entity.vo

import jakarta.validation.constraints.NotEmpty

open class UploadResult {
    var success:Boolean = false
    var message:String? = null
    var url:String? = null

    @NotEmpty(message = "文件UID不能为空")
    var fileUID:String? = null

    @NotEmpty(message = "存储类型不能为空")
    lateinit var storageType:String

    @NotEmpty(message = "存储路径不能为空")
    lateinit var path:String
}