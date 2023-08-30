package xyz.chener.zp.storagev2.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import org.springframework.stereotype.Service
import xyz.chener.zp.storagev2.dao.FileSystemMap2Dao
import xyz.chener.zp.storagev2.entity.FileSystemMap2
import xyz.chener.zp.storagev2.service.FileSystemMap2Service


@Service
open class FileSystemMap2ServiceImpl : ServiceImpl<FileSystemMap2Dao,   FileSystemMap2>(), FileSystemMap2Service