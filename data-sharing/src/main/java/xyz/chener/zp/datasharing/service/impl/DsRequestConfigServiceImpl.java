package xyz.chener.zp.datasharing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.chener.zp.datasharing.dao.DsRequestConfigDao;
import xyz.chener.zp.datasharing.entity.DsRequestConfig;
import xyz.chener.zp.datasharing.entity.dto.DsRequestConfigDto;
import xyz.chener.zp.datasharing.service.DsRequestConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

import static xyz.chener.zp.datasharing.service.impl.DataSharingServiceImpl.RequestLimitKeyPrefix;

/**
 * (DsRequestConfig)表服务实现类
 *
 * @author makejava
 * @since 2023-04-02 10:23:55
 */
@Service
public class DsRequestConfigServiceImpl extends ServiceImpl<DsRequestConfigDao, DsRequestConfig> implements DsRequestConfigService {

    private RedissonClient redissonClient;

    @Autowired
    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public PageInfo<DsRequestConfigDto> getDsRequestConfigList(DsRequestConfigDto dsRequestConfigDto, int page, int size) {

        PageHelper.startPage(page, size);
        List<DsRequestConfigDto> list = getBaseMapper().getRequestConfigList(dsRequestConfigDto);
        list.forEach(e->{
            String key = RequestLimitKeyPrefix + e.getRequestUid() + ":" + e.getId();
            RAtomicLong count = redissonClient.getAtomicLong(key);
            e.setDayCount(String.valueOf(count.get()));
        });
        return new PageInfo<>(list);
    }
}

