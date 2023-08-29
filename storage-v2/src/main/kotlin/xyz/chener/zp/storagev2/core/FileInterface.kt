package xyz.chener.zp.storagev2.core

import xyz.chener.zp.common.config.ctx.ApplicationContextHolder


interface FileInterface {

    companion object {
        fun get (type: FileStorageTypeEnum): FileInterface? {
            return null;
        }

        fun get(type:String):FileInterface?{
            return FileStorageTypeEnum.get(type)?.let {
                return@let get(it)
            }
        }


    }


    fun save() : Boolean

    fun getUrl():String

}