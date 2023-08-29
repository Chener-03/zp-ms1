package xyz.chener.zp.storagev2.core

enum class FileStorageTypeEnum(val type:String) {

    QINIU("QINIU"),
    MINIO("MINIO"),
    ;

    companion object {
        fun get (type: String): FileStorageTypeEnum? {
            for (value in values()) {
                if (value.type == type) {
                    return value
                }
            }
            return null
        }
    }

}