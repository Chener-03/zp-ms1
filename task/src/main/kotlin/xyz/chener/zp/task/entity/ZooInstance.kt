package xyz.chener.zp.task.entity

open class ZooInstance(var ip:String,var address:String, var createTime : Long, var processCore:Int, var memory:Long, var appProcessUid:String?,var jobInstanceId:String?){
    constructor() : this("","", 0, 0, 0, null,null)
}