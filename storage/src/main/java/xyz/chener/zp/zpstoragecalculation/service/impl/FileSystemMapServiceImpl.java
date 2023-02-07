package xyz.chener.zp.zpstoragecalculation.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.util.StreamUtils;
import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.error.HttpErrorException;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.TransactionUtils;
import xyz.chener.zp.zpstoragecalculation.config.StorageProperties;
import xyz.chener.zp.zpstoragecalculation.dao.FileSystemMapDao;
import xyz.chener.zp.zpstoragecalculation.entity.FileSystemMap;
import xyz.chener.zp.zpstoragecalculation.entity.dto.UploadResultDto;
import xyz.chener.zp.zpstoragecalculation.error.FileNotExitsException;
import xyz.chener.zp.zpstoragecalculation.service.FileSystemMapService;
import xyz.chener.zp.zpstoragecalculation.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;

/**
 * (FileSystemMap)表服务实现类
 *
 * @author makejava
 * @since 2023-01-22 22:26:34
 */
@Service
public class FileSystemMapServiceImpl extends ServiceImpl<FileSystemMapDao, FileSystemMap> implements FileSystemMapService {

    private final FileUtils fileUtils;
    private final RedissonClient redissonClient;

    private final DataSourceTransactionManager dataSourceTransactionManager;
    private final StorageProperties storageProperties;

    public FileSystemMapServiceImpl(FileUtils fileUtils
            , RedissonClient redissonClient
            , DataSourceTransactionManager dataSourceTransactionManager
            , StorageProperties storageProperties) {
        this.fileUtils = fileUtils;
        this.redissonClient = redissonClient;
        this.dataSourceTransactionManager = dataSourceTransactionManager;
        this.storageProperties = storageProperties;
    }



    @Override
    public UploadResultDto uploadFile(Long userId, String fileName, byte[] data) {
        UploadResultDto res = new UploadResultDto(false);
        TransactionDefinition td = TransactionUtils.getTransactionDefinition();
        TransactionStatus transaction = dataSourceTransactionManager.getTransaction(td);
        String resourceUid = UUID.randomUUID().toString().replace("-", "");
        RLock lock = redissonClient.getLock(resourceUid);
        try {
            lock.lock();
            AssertUrils.state(data.length!=0, new HttpErrorException(UploadResultDto.FailReason.FILE_IS_NULL));
            FileSystemMap fm = new FileSystemMap();
            fm.setUserId(userId);
            fm.setFileMd5(fileUtils.getDataMd5(data));
            fm.setFileSize(data.length);
            fm.setCreateTime(new Date());
            fm.setFileName(fileName);
            fm.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
            fm.setVisitNum(0);
            fm.setResourceUid(resourceUid);
            fm.setSystemUid(storageProperties.getHardwareUid());
            this.save(fm);
            res.setSuccess(true);
            res.setFileSystemMap(fm);
            AssertUrils.state(fileUtils.save(fm.getResourceUid(), data), new HttpErrorException(UploadResultDto.FailReason.FILE_WRITE_ERROR));
            dataSourceTransactionManager.commit(transaction);
        }catch (Exception e){
            res.setSuccess(false);
            res.setFileSystemMap(null);
            dataSourceTransactionManager.rollback(transaction);
            res.setMessage(e.getMessage());
        }finally {
            lock.unlock();
        }
        return res;
    }

    @Override
    public void getLocalFile(HttpServletResponse response, String resourceUid, String filename) {
        RLock lock = redissonClient.getLock(resourceUid);
        lock.lock();
        try {
            String absolutePath = new File(storageProperties.getLocation()).getAbsolutePath();
            String path = absolutePath + File.separator + resourceUid;
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                try (InputStream is = Files.newInputStream(file.toPath());
                     ServletOutputStream os = response.getOutputStream()) {
                    response.reset();
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.addHeader(HttpHeaders.CONTENT_DISPOSITION
                            , "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
                    response.addHeader(HttpHeaders.CONTENT_LENGTH, "" + file.length());
                    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                    StreamUtils.copy(is,os);
                } catch (IOException ignored) {}
            }else
            {
                try ( ServletOutputStream os = response.getOutputStream())
                {
                    response.reset();
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    FileNotExitsException e = new FileNotExitsException();
                    ObjectMapper om = new ObjectMapper();
                    String s = om.writeValueAsString(R.Builder.getInstance().setCode(e.getHttpCode())
                            .setMessage(e.getMessage()).build());
                    os.write(s.getBytes(StandardCharsets.UTF_8));
                }catch (Exception ignored){ }
            }
        }catch (Exception ig){}
        finally {
            lock.unlock();
        }
    }
}

