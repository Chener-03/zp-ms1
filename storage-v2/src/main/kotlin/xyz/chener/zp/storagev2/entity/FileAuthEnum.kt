package xyz.chener.zp.storagev2.entity

class FileAuthEnum {

    companion object {

        // 任何人可读
        const val PUBLIC_READ = 1

        // 创建者可读
        const val USER_READ = 2

        // 管理员可读
        const val ADMIN_READ = 4

        // 任何人可写
        const val PUBLIC_WRITE = 8

        // 创建者可写
        const val USER_WRITE = 16

        // 管理员可写
        const val ADMIN_WRITE = 32


        fun default():Int{
            return PUBLIC_READ or USER_WRITE or ADMIN_WRITE
        }

        fun contain(allAuth:Int,authEnum: Int):Boolean{
            return (allAuth and  authEnum)!=0
        }


    }

}