package xyz.chener.zp.zpusermodule.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.chener.zp.zpusermodule.entity.Notices;
import xyz.chener.zp.zpusermodule.entity.dto.NoticesDto;

import java.util.List;

/**
 * (Notices)表服务接口
 *
 * @author makejava
 * @since 2023-03-04 18:21:14
 */
public interface NoticesService extends IService<Notices> {

    Boolean publish( NoticesDto dto,List<String> userNames,List<String> ditchs);

}

