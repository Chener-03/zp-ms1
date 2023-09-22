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

open class ZookeeperProxy : ZooKeeper,DisposableBean  {

    private val log : Logger = LoggerFactory.getLogger(ZookeeperProxy::class.java)

    private var taskConfiguration : TaskConfiguration

    @Throws(IOException::class)
     constructor(connectString: String?,sessionTimeout: Int,watcher: Watcher?,taskConfiguration : TaskConfiguration):super(connectString, sessionTimeout, watcher){
        this.taskConfiguration = taskConfiguration
        checkRootDirExist()
    }

    @Throws(IOException::class)
    constructor(connectString: String?,sessionTimeout: Int,watcher: Watcher?,conf : ZKClientConfig?,taskConfiguration : TaskConfiguration):super(connectString, sessionTimeout, watcher,conf){
        this.taskConfiguration = taskConfiguration
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
        return taskConfiguration.zk.digestACL?.let {
            return@let it.stream().map { it1 ->
                ACL(ZooDefs.Perms.ALL, Id("digest", it1))
            }.toList()
        } ?:ZooDefs.Ids.OPEN_ACL_UNSAFE
    }


    override fun destroy() {
        this.close()
    }
}