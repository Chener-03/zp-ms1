package xyz.chener.zp.task.core

import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

open class SimpleJobHandleProxy<T>(source:T)
: InvocationHandler {

    private var source:T

    init {
        this.source = source
    }

    override fun invoke(proxy: Any, method: Method?, vararg args: Any): Any? {
        return method!!.invoke(source,*args)
    }
}