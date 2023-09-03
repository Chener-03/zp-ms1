package xyz.chener.zp.common.utils



import kotlin.reflect.KMutableProperty1
import kotlin.reflect.jvm.javaGetter


open class ObjectUtilsKt : ObjectUtils() {

    companion object {

        @JvmStatic
        fun <T> objectFieldsEqualsKt(o1: T, o2: T, vararg fields: KMutableProperty1<T, *>): Boolean {
            if (o1 == null || o2 == null) {
                return o1 == o2
            }
            if (o1!!::class != o2!!::class) {
                return false;
            }

            if (fields.isEmpty()) {
                val filter = o1!!::class.members.stream().filter {
                    it is KMutableProperty1<*, *>
                }.toList()

                filter.forEach{
                    if (it.call(o1) != it.call(o2)){
                        return@objectFieldsEqualsKt false
                    }
                }
            }

            fields.forEach {
                if (it.call(o1) != it.call(o2)){
                    return@objectFieldsEqualsKt false
                }
            }
            return true
        }


    }
}



