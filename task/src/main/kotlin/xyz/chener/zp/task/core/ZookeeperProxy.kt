package xyz.chener.zp.task.core

import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.KeeperException
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.client.ZKClientConfig
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.data.Id
import org.apache.zookeeper.data.Stat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import xyz.chener.zp.task.config.TaskConfiguration
import java.io.IOException

open class ZookeeperProxy
@Throws(IOException::class)
constructor(
    private val connectString: String?,
    private val sessionTimeout: Int,
    private val watcher: Watcher?,
    private var taskConfiguration: TaskConfiguration
) : ZooKeeper(connectString, sessionTimeout, watcher),DisposableBean  {

    private val log : Logger = LoggerFactory.getLogger(ZookeeperProxy::class.java)


    init {
        taskConfiguration.zk.digestACL?.let {
            this.addAuthInfo("digest", it.toByteArray())
        }
        checkRootDirExist()
    }

    open fun getRootDir():String{
        return this.taskConfiguration.zk.vitureDir
    }

    private fun checkRootDirExist(){
        val exists : Stat? = this.exists(this.getRootDir(), false)
        if (exists == null){
            create(getRootDir(),null,getAcl(),CreateMode.PERSISTENT)
        }
    }

    open fun getAcl() : List<ACL> {
        return ZooDefs.Ids.OPEN_ACL_UNSAFE
    }


    override fun destroy() {
        this.close()
    }
}