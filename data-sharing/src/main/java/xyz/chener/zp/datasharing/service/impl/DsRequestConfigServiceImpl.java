package xyz.chener.zp.datasharing.service.impl;

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.datasharing.connect.DBConnectorManager;
import xyz.chener.zp.datasharing.connect.entity.DataSourceStruce;
import xyz.chener.zp.datasharing.dao.DsRequestConfigDao;
import xyz.chener.zp.datasharing.entity.DsRequestConfig;
import xyz.chener.zp.datasharing.entity.dto.DsDatasourceDto;
import xyz.chener.zp.datasharing.entity.dto.DsRequestConfigDto;
import xyz.chener.zp.datasharing.error.config.BindDatasourceNotFount;
import xyz.chener.zp.datasharing.error.config.SqlRunError;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.SqlPe;
import xyz.chener.zp.datasharing.requestProcess.exec.SqlExec;
import xyz.chener.zp.datasharing.service.DsDatasourceService;
import xyz.chener.zp.datasharing.service.DsRequestConfigService;
import org.springframework.stereotype.Service;
import xyz.chener.zp.datasharing.utils.SqlUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
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

    private DsDatasourceService dsDatasourceService;
    private DBConnectorManager dbConnectorManager;

    @Autowired
    public void setDbConnectorManager(DBConnectorManager dbConnectorManager) {
        this.dbConnectorManager = dbConnectorManager;
    }

    @Autowired
    public void setDsDatasourceService(DsDatasourceService dsDatasourceService) {
        this.dsDatasourceService = dsDatasourceService;
    }

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

    @Override
    public List<String>[] getSqlResultParam(String sql, Integer datasourceId) {
        FieldQuery fq = new FieldQuery();
        fq.getQueryFields().add("id");
        DsDatasourceDto dsd = new DsDatasourceDto();
        dsd.setId(datasourceId);
        PageInfo<DsDatasourceDto> list = dsDatasourceService.getList(dsd, fq, 1, 10);
        AssertUrils.state(list.getTotal()>0, BindDatasourceNotFount.class);

        DataSourceStruce dataSource = dbConnectorManager.getDataSource(datasourceId);
        AssertUrils.state(dataSource != null, BindDatasourceNotFount.class);

        List<String>[] res = new List[2];
        res[0] = new ArrayList<>();
        res[1] = new ArrayList<>();

        try (Connection connection = dataSource.getDataSource().getConnection()) {
            String sqlType = SqlUtils.getSqlType(sql);
            AssertUrils.state(sqlType!= null,new RuntimeException("sql语句不合法"));
            if (sqlType.equalsIgnoreCase(SqlPe.TYPE_SELECT)){
                ResultSet resultSet = connection.createStatement().executeQuery(sql);
                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i + 1);
                    res[0].add(columnName);
                    res[1].add(SqlUtils.toCamelCase(columnName));
                }
            }else {
                res[0].add(SqlExec.UPDATE_COLUMN_NAME);
                res[1].add(SqlUtils.toCamelCase(SqlExec.UPDATE_COLUMN_NAME));
            }
        }catch (Exception e) {
            throw new SqlRunError(e.getMessage());
        }

        return res;
    }
}

