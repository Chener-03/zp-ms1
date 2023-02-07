package xyz.chener.zp.zpstoragecalculation.controller;


import com.github.pagehelper.PageInfo;
import feign.Response;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StreamUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.chener.zp.common.config.UnifiedReturn;
import xyz.chener.zp.common.entity.WriteList;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.zpstoragecalculation.config.StorageProperties;
import xyz.chener.zp.zpstoragecalculation.config.feign.loadbalance.Instance;
import xyz.chener.zp.zpstoragecalculation.config.feign.loadbalance.LoadbalancerContextHolder;
import xyz.chener.zp.zpstoragecalculation.entity.FileSystemMap;
import xyz.chener.zp.zpstoragecalculation.entity.dto.UploadResultDto;
import xyz.chener.zp.zpstoragecalculation.entity.dto.UserAllInfoDto;
import xyz.chener.zp.zpstoragecalculation.error.FileNotExitsException;
import xyz.chener.zp.zpstoragecalculation.error.FileServerDowmException;
import xyz.chener.zp.zpstoragecalculation.error.FileTransferException;
import xyz.chener.zp.zpstoragecalculation.service.FileSystemModluleService;
import xyz.chener.zp.zpstoragecalculation.service.UserModuleService;
import xyz.chener.zp.zpstoragecalculation.service.impl.FileSystemMapServiceImpl;
import xyz.chener.zp.zpstoragecalculation.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class FileController {


    private final FileSystemMapServiceImpl fileSystemMapService;
    private final FileUtils fileUtils;
    private final UserModuleService userModuleService;
    private final StorageProperties storageProperties;
    private final FileSystemModluleService fileSystemModluleService;

    public FileController(FileSystemMapServiceImpl fileSystemMapService, FileUtils fileUtils
            , @Qualifier("xyz.chener.zp.zpstoragecalculation.service.UserModuleService") UserModuleService userModuleService, StorageProperties storageProperties, FileSystemModluleService fileSystemModluleService) {
        this.fileSystemMapService = fileSystemMapService;
        this.fileUtils = fileUtils;
        this.userModuleService = userModuleService;
        this.storageProperties = storageProperties;
        this.fileSystemModluleService = fileSystemModluleService;
    }

    @PostMapping("/uploadFilePrivate")
    @PreAuthorize("hasAnyRole('file_private')")
    public UploadResultDto uploadFilePrivate(@RequestParam String filename
            , @RequestParam MultipartFile file
            , @RequestParam @Length(min = 3,max = 20,message = "用户名长度3-20")  String username) {
        UploadResultDto res = new UploadResultDto(false);
        if (SecurityContextHolder.getContext().getAuthentication()!=null)
        {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!name.equals(username)) {
                res.setMessage(UploadResultDto.FailReason.USER_NOT_MATCH);
                return res;
            }
        }
        PageInfo<UserAllInfoDto> userInfo = userModuleService.getUserAllInfo(username, false);
        if (userInfo.getList().size() == 0) {
            res.setMessage(UploadResultDto.FailReason.USER_NOT_FOUND);
            return res;
        }
        try {
            return fileSystemMapService.uploadFile(userInfo.getList().get(0).getId(), filename, file.getBytes());
        } catch (Exception e) {
            throw new FileTransferException();
        }
    }

    @PostMapping("/uploadFilePublic")
    @PreAuthorize("hasAnyRole('file_public')")
    public UploadResultDto uploadFilePublic(@RequestParam String filename
            , @RequestParam MultipartFile file ) {
        try {
            return fileSystemMapService.uploadFile(null, filename, file.getBytes());
        } catch (IOException e) {
            throw new FileTransferException();
        }
    }


    @GetMapping("/getFilePublic")
    @WriteList
    public void getFilePublic(HttpServletResponse response,@RequestParam String resourceUid)
    {
        FileSystemMap file = fileSystemMapService.lambdaQuery().eq(FileSystemMap::getResourceUid, resourceUid)
                .isNull(FileSystemMap::getUserId).last("limit 1").one();
        get(response, resourceUid,  file);
    }

    @GetMapping("/getFilePrivate")
    @PreAuthorize("hasAnyRole('file_private')")
    public void getFilePrivate(HttpServletResponse response
            ,@RequestParam String resourceUid,@RequestParam Integer userId)
    {
        FileSystemMap file = fileSystemMapService.lambdaQuery().eq(FileSystemMap::getResourceUid, resourceUid)
                .eq(FileSystemMap::getUserId,userId).last("limit 1").one();
        get(response, resourceUid,  file);
    }

    private void get(HttpServletResponse response,String resourceUid, FileSystemMap file) {
        AssertUrils.state(file!=null, FileNotExitsException.class);
        if (Objects.equals(file.getSystemUid(),storageProperties.getHardwareUid())) {
            this.getLocalFile(response, resourceUid, file.getFileName() );
        }else
        {
            LoadbalancerContextHolder.setNextInstance(new Instance(file.getSystemUid()));
            Response resp = fileSystemModluleService.getLocalFile(resourceUid, file.getFileName());
            AssertUrils.state(resp.status()==200, FileServerDowmException.class);
            addHeader(HttpHeaders.CONTENT_DISPOSITION,resp,response);
            addHeader(HttpHeaders.CONTENT_LENGTH,resp,response);
            addHeader(HttpHeaders.CONTENT_TYPE,resp,response);
            try (InputStream is = resp.body().asInputStream();
                 OutputStream os = response.getOutputStream()){
                StreamUtils.copy(is,os);
            }catch (Exception exception){}
        }
    }

    private void addHeader(String headName,Response resp,HttpServletResponse response)
    {
        resp.headers().get(headName).forEach(s -> response.addHeader(headName, s));
    }

    @GetMapping("/getLocalFiles")
    @PreAuthorize("hasAnyRole('microservice_call')")
    public void getLocalFile(HttpServletResponse response,@RequestParam String resourceUid,@RequestParam String filename)
    {
        fileSystemMapService.getLocalFile(response, resourceUid, filename);
    }


}
