package xyz.chener.zp.zpstoragecalculation.entity.dto;

import xyz.chener.zp.zpstoragecalculation.entity.FileSystemMap;

public class UploadResultDto {

    public static class FailReason{
        public static final String FILE_WRITE_ERROR = "文件写入失败";
        public static final String FILE_IS_NULL = "空文件";
        public static final String USER_NOT_FOUND = "对应用户未找到";
        public static final String USER_NOT_MATCH = "用户不匹配";
    }

    public UploadResultDto() {
    }

    public UploadResultDto(boolean success) {
        this.success = success;
    }

    private boolean success;

    private String message;

    private FileSystemMap fileSystemMap;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FileSystemMap getFileSystemMap() {
        return fileSystemMap;
    }

    public void setFileSystemMap(FileSystemMap fileSystemMap) {
        this.fileSystemMap = fileSystemMap;
    }
}
