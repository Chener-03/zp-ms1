package xyz.chener.zp.task.core

import org.apache.zookeeper.CreateMode
import org.apache.zookeeper.KeeperException
import org.apache.zookeeper.Watcher
import org.apache.zookeeper.ZooDefs
import org.apache.zookeeper.ZooKeeper
import org.apache.zookeeper.client.ZKClientConfig
import org.apache.zookeeper.data.ACL
import org.apache.zookeeper.data.Stat
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import xyz.chener.zp.task.config.TaskConfiguration
import java.io.IOException

class ZookeeperProxy : ZooKeeper  {

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


    fun getRootDir():String{
        return taskConfiguration.zk.vitureDir
    }

    private fun checkRootDirExist(){
        val exists : Stat? = this.exists(this.getRootDir(), false)
        if (exists == null){
            create(getRootDir(),null,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT)
        }
    }

}