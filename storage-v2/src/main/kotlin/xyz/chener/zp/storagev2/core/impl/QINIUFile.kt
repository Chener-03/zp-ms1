package xyz.chener.zp.storagev2.core.impl

import org.springframework.stereotype.Service
import xyz.chener.zp.storagev2.core.FileInterface


@Service("xyz.chener.zp.storagev2.core.impl.QINIUFile")
class QINIUFile : FileInterface {
    override fun save(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getUrl(): String {
        TODO("Not yet implemented")
    }
}