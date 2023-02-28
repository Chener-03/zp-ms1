package xyz.chener.zp.zpusermodule.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import xyz.chener.zp.zpusermodule.dao.UserLoginEventRecordDao;
import xyz.chener.zp.zpusermodule.entity.UserLoginEventRecord;
import xyz.chener.zp.zpusermodule.entity.dto.UserLoginEventRecordDto;
import xyz.chener.zp.zpusermodule.service.UserLoginEventRecordService;
import xyz.chener.zp.zpusermodule.utils.Ip2RegUtils;

import java.util.List;

/**
 * (UserLoginEventRecord)表服务实现类
 *
 * @author makejava
 * @since 2023-01-16 15:59:34
 */
@Service
public class UserLoginEventRecordServiceImpl extends ServiceImpl<UserLoginEventRecordDao, UserLoginEventRecord> implements UserLoginEventRecordService {

    private final Ip2RegUtils ip2RegUtils;

    public UserLoginEventRecordServiceImpl(Ip2RegUtils ip2RegUtils) {
        this.ip2RegUtils = ip2RegUtils;
    }

    @Override
    public PageInfo<UserLoginEventRecordDto> getList(UserLoginEventRecordDto dto, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<UserLoginEventRecordDto> list = getBaseMapper().getList(dto);
        list.forEach(e->{
            e.setAddress(ip2RegUtils.getReg(e.getIp()).toString());
        });
        return new PageInfo<>(list);
    }
}

