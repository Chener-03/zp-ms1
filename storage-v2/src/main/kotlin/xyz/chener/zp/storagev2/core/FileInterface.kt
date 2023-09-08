package xyz.chener.zp.storagev2.core

import xyz.chener.zp.common.config.ctx.ApplicationContextHolder


interface FileInterface {

    companion object {
        fun get (type: FileStorageTypeEnum): FileInterface? {
            return ApplicationContextHolder.getApplicationContext().getBean(type.clazzStr) as? FileInterface?
        }

        fun get(type:String):FileInterface?{
            return FileStorageTypeEnum.get(type)?.let {
                return@let get(it)
            }
        }


    }


    /**
     * 保存文件
     */
    fun save(data:ByteArray,path:String) : Boolean


    /**
     * 获取文件url
     */
    fun getUrl(file:String):String?

    /**
     * 上传文件url
     */
    fun saveUrl(file: String):String?


    /**
     * 删除文件
     */
    fun delete(file:String):Boolean

    /**
     * 文件是否存在
     */
    fun exist(file:String):Boolean

}