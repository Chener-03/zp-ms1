package xyz.chener.zp.storagev2.core.impl

import org.springframework.stereotype.Service
import xyz.chener.zp.storagev2.core.FileInterface


@Service("xyz.chener.zp.storagev2.core.impl.QINIUFile")
open class QINIUFile : FileInterface {
    override fun save(data: ByteArray, path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun exist(file: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun delete(file: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun saveUrl(file: String): String? {
        TODO("Not yet implemented")
    }

    override fun getUrl(file: String): String? {
        TODO("Not yet implemented")
    }
}