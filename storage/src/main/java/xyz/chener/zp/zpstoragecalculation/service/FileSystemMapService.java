package xyz.chener.zp.zpstoragecalculation.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.zpstoragecalculation.entity.FileSystemMap;
import xyz.chener.zp.zpstoragecalculation.entity.dto.UploadResultDto;

/**
 * (FileSystemMap)表服务接口
 *
 * @author makejava
 * @since 2023-01-22 22:26:34
 */
public interface FileSystemMapService extends IService<FileSystemMap> {

    UploadResultDto uploadFile(Long userId, String fileName,byte[] data);


    void getLocalFile(HttpServletResponse response, @RequestParam String resourceUid, @RequestParam String filename);

}

