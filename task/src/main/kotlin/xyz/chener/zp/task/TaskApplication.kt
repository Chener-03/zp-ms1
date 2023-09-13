package xyz.chener.zp.task

import com.alibaba.cloud.sentinel.endpoint.SentinelEndpointAutoConfiguration
import com.sun.jdi.*
import com.sun.jdi.connect.AttachingConnector
import com.sun.jdi.connect.Connector
import com.sun.jdi.connect.IllegalConnectorArgumentsException
import com.sun.jdi.event.ExceptionEvent
import com.sun.jdi.event.MethodEntryEvent
import com.sun.jdi.event.MethodExitEvent
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.transaction.annotation.EnableTransactionManagement
import xyz.chener.zp.task.core.entity.TaskData
import xyz.chener.zp.task.core.op.Win32System
import xyz.chener.zp.task.core.target.JarTaskExecute
import xyz.chener.zp.task.core.target.JarTaskMBean
import java.io.IOException
import java.lang.management.ManagementFactory
import javax.management.JMX
import javax.management.MBeanInfo
import javax.management.MBeanParameterInfo
import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL
import kotlin.reflect.KClass
import kotlin.reflect.full.functions
import kotlin.reflect.javaType

@org.springframework.boot.autoconfigure.SpringBootApplication(exclude = [SentinelEndpointAutoConfiguration::class]
    , excludeName = ["org.redisson.spring.starter.RedissonAutoConfiguration"])
@EnableFeignClients
@EnableTransactionManagement
open class TaskApplication {

    companion object {
/*
        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty("csp.sentinel.log.output.type", "console")
//            org.springframework.boot.runApplication<TaskApplication>(*args)

            class RestrictedClassLoader(urls: String) : URLClassLoader(arrayOf(File(urls).toURI().toURL())) {

                // 在加载类之前实施访问控制逻辑
                override fun loadClass(name: String, resolve: Boolean): Class<*> {
                    if (isNotAllowed(name)) {
                        throw SecurityException("Access to class $name is not allowed.")
                    }
                    return super.loadClass(name, resolve)
                }

                private fun isNotAllowed(className: String): Boolean {
                    // 添加您的逻辑，检查是否允许加载特定的类
                    return false // 根据您的逻辑返回 true 或 false
                }
            }


//            val classLoader = RestrictedClassLoader("D:\\code\\ZP\\ZP\\zp-ms1\\gateway\\build\\libs\\gateway-1.0-SNAPSHOT.jar")
//            val loadClass = classLoader.loadClass("xyz.chener.zp.zpgateway.ZpGatewayApplication")
//            val newInstance = loadClass.getConstructor().newInstance()
            val vm:VirtualMachine? = null
            try {
                val vm: VirtualMachine = connectToTarget("localhost", 9988)
                val securityRepository = findClass(vm, "xyz.chener.testaaaa.Main")
                val filtermethod = securityRepository.methodsByName("aa")[0]


                val eventRequestManager = vm.eventRequestManager()

//                eventRequestManager.createBreakpointRequest()
                val methodEntryRequest = eventRequestManager.createMethodEntryRequest()

                methodEntryRequest.addClassFilter(securityRepository)
                methodEntryRequest.enable()


                val ret = eventRequestManager.createMethodExitRequest()
                ret.addClassFilter(securityRepository)
                ret.enable()

                val exc = eventRequestManager.createExceptionRequest(null, true, true)
                exc.addClassFilter(securityRepository)
                exc.enable()



                waitForMethodEntryEvents(vm)
                println()
            }catch (e:Exception){
                vm?.dispose()
            }
        }

*/

        private fun waitForMethodEntryEvents(vm: VirtualMachine) {
            while (true) {
                try {
                    val eventSet = vm.eventQueue().remove()
                    for (event in eventSet) {
                        println(event.toString())
                        if (event is MethodEntryEvent) {
                            if (event.method().name().equals("aa")){
                                val stackFrame: StackFrame = event.thread().frame(0)
                                val field = event.method().declaringType().fieldByName("error")
                                val value:Value = stackFrame.thisObject().getValue(field)
                                stackFrame.thisObject().setValue(field, vm.mirrorOf(true))



                                println()
                            }
                        }

                        if (event is MethodExitEvent){
                            if (event.method().name().equals("aa")){


                                println()
                            }
                        }

                        if (event is ExceptionEvent){


                            println()
                        }

                    }
                    eventSet.resume()
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }

        private fun connectToTarget(hostname: String, port: Int): VirtualMachine {
            val connector: AttachingConnector = Bootstrap.virtualMachineManager().attachingConnectors()
                .stream()
                .filter { c -> c.name().equals("com.sun.jdi.SocketAttach") }
                .findFirst()
                .orElseThrow { RuntimeException("Connector not found") }
            val arguments: Map<String, Connector.Argument> = connector.defaultArguments()
            arguments["hostname"]?.setValue(hostname)
            arguments["port"]?.setValue(port.toString())
            return try {
                connector.attach(arguments)
            } catch (e: IOException) {
                throw RuntimeException("Failed to connect to target VM", e)
            } catch (e: IllegalConnectorArgumentsException) {
                throw RuntimeException("Failed to connect to target VM", e)
            }
        }


        private fun findClass(vm: VirtualMachine, className: String): ReferenceType {
            val classes: List<ReferenceType> = vm.classesByName(className)
            if (classes.isEmpty()) {
                throw java.lang.RuntimeException("Class not found: $className")
            }
            return classes[0]
        }


        @JvmStatic
        fun main(args: Array<String>) {


            val hostname = "localhost"

            val port = 12345
            val jmxUrl = "service:jmx:rmi:///jndi/rmi://$hostname:$port/jmxrmi"

            val serviceURL = JMXServiceURL(jmxUrl)
            val connector = JMXConnectorFactory.connect(serviceURL)
            val mbsc = connector.mBeanServerConnection

            val name = ObjectName(JarTaskExecute.REG_PATH)

            val funSignature = getFunSignature(JarTaskMBean::class, JarTaskMBean::handle.name)


            val invoke = mbsc.invoke(name, JarTaskMBean::toOutput.name, null, null)


            val proxy = JMX.newMXBeanProxy(mbsc, name, JarTaskMBean::class.java)
            proxy.handle(TaskData(),1)


            connector.close()
        }


        private fun getFunSignature(clazz:KClass<*>, funName:String) : Array<String> {
            val kFunction = clazz.functions.find { it.name == funName } ?: return arrayOf()
            return kFunction.parameters.filter {
//                it.kind.name == "VALUE"
                true
            }.map {
                it.type.toString()
            }.toTypedArray()
        }

    }

}