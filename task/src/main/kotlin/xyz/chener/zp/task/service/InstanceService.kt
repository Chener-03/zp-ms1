package xyz.chener.zp.task.service

import xyz.chener.zp.task.entity.ZooInstance

interface InstanceService {
    fun getOnlineInstance():List<ZooInstance>;
}