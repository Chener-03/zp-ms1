package xyz.chener.zp.zpstoragecalculation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.util.Date;

/**
 * (FileSystemMap)表实体类
 *
 * @author makejava
 * @since 2023-01-22 22:34:28
 */
@SuppressWarnings("serial")
public class FileSystemMap extends Model<FileSystemMap> {

    @TableId(type = IdType.AUTO)
    private Long id;
    //文件id
    private String resourceUid;
    //分布式系统id
    private String systemUid;
    
    private Date createTime;
    
    private String fileMd5;
    
    private String fileName;
    
    private String fileType;
    
    private Integer visitNum;
    
    private Integer fileSize;

    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceUid() {
        return resourceUid;
    }

    public void setResourceUid(String resourceUid) {
        this.resourceUid = resourceUid;
    }

    public String getSystemUid() {
        return systemUid;
    }

    public void setSystemUid(String systemUid) {
        this.systemUid = systemUid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Integer getVisitNum() {
        return visitNum;
    }

    public void setVisitNum(Integer visitNum) {
        this.visitNum = visitNum;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }


    }

