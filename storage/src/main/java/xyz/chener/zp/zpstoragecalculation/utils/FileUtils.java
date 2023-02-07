package xyz.chener.zp.zpstoragecalculation.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import xyz.chener.zp.zpstoragecalculation.config.StorageProperties;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;

@Component
@Slf4j
public class FileUtils {

    private final StorageProperties storageProperties;

    public FileUtils(StorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    public boolean save(String filename, byte[] data){
        String absolutePath = new File(storageProperties.getLocation()).getAbsolutePath();
        if (filename.indexOf("/")==0 || filename.indexOf("\\")==0) {
            filename = filename.substring(1);
        }
        String path = absolutePath + File.separator + filename;
        try {
            Files.write(Path.of(path), data, StandardOpenOption.CREATE);
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }


    public String getDataMd5(byte[] data){
        String md5 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            md5 = sb.toString();
        }catch (Exception ignored) { }
        return md5;
    }

}
