package xyz.chener.zp.common.utils

import java.util.Objects
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KMutableProperty1


open class ObjectUtilsKt : ObjectUtils() {

    class TEST {
        var aaa: String = ""
    }


    companion object {

        fun <T> objectFieldsEqualsKt(o1: T, o2: T, vararg fields: KMutableProperty1<T, *>): Boolean {
            if (o1 == null || o2 == null) {
                return o1 == o2
            }

            if (!o1!!::class.equals(o2!!::class)) {
                return false;
            }
//o1!!::class.java.getMethod(fields[0]!!.javaGetter.name).invoke(o2)
            println()
            return true
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val test1 = TEST()
            val test2 = TEST()
            test1.aaa = "111"
            test2.aaa = "222"
            objectFieldsEqualsKt(test1, test2, TEST::aaa)

        }


    }

}