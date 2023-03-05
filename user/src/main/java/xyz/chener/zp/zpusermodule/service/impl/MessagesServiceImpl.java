package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import xyz.chener.zp.zpusermodule.dao.MessagesDao;
import xyz.chener.zp.zpusermodule.entity.Messages;
import xyz.chener.zp.zpusermodule.service.MessagesService;
import org.springframework.stereotype.Service;

/**
 * (Messages)表服务实现类
 *
 * @author makejava
 * @since 2023-03-05 12:31:57
 */
@Service
public class MessagesServiceImpl extends ServiceImpl<MessagesDao, Messages> implements MessagesService {

}

