package xyz.chener.zp.datasharing.service.impl;

import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.redisson.Redisson;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.common.utils.ThreadUtils;
import xyz.chener.zp.common.utils.TransactionUtils;
import xyz.chener.zp.datasharing.connect.DBConnectorManager;
import xyz.chener.zp.datasharing.connect.entity.DataSourceStruce;
import xyz.chener.zp.datasharing.dao.DsRequestConfigDao;
import xyz.chener.zp.datasharing.dao.DsRequestProcessConfigDao;
import xyz.chener.zp.datasharing.entity.DsRequestConfig;
import xyz.chener.zp.datasharing.entity.DsRequestProcessConfig;
import xyz.chener.zp.datasharing.entity.dto.DsDatasourceDto;
import xyz.chener.zp.datasharing.entity.dto.DsRequestConfigAllDto;
import xyz.chener.zp.datasharing.entity.dto.DsRequestConfigDto;
import xyz.chener.zp.datasharing.entity.thirdparty.UserBase;
import xyz.chener.zp.datasharing.error.config.BindDatasourceNotFount;
import xyz.chener.zp.datasharing.error.config.DsRequestConfigNotFoundError;
import xyz.chener.zp.datasharing.error.config.SqlRunError;
import xyz.chener.zp.datasharing.requestProcess.entity.RequestProcessType;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.*;
import xyz.chener.zp.datasharing.requestProcess.exec.SqlExec;
import xyz.chener.zp.datasharing.service.DsDatasourceService;
import xyz.chener.zp.datasharing.service.DsRequestConfigService;
import org.springframework.stereotype.Service;
import xyz.chener.zp.datasharing.service.DsRequestProcessConfigService;
import xyz.chener.zp.datasharing.service.UserModuleService;
import xyz.chener.zp.datasharing.utils.SqlUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
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

    private DataSourceTransactionManager dataSourceTransactionManager;

    private DsRequestProcessConfigService dsRequestProcessConfigService;
    private DsRequestProcessConfigDao dsRequestProcessConfigDao;
    private UserModuleService userModuleService;

    @Autowired
    @Qualifier("xyz.chener.zp.datasharing.service.UserModuleService")
    public void setUserModuleService(UserModuleService userModuleService) {
        this.userModuleService = userModuleService;
    }

    @Autowired
    public void setDsRequestProcessConfigDao(DsRequestProcessConfigDao dsRequestProcessConfigDao) {
        this.dsRequestProcessConfigDao = dsRequestProcessConfigDao;
    }

    @Autowired
    public void setDsRequestProcessConfigService(DsRequestProcessConfigService dsRequestProcessConfigService) {
        this.dsRequestProcessConfigService = dsRequestProcessConfigService;
    }

    @Autowired
    public void setDataSourceTransactionManager(DataSourceTransactionManager dataSourceTransactionManager) {
        this.dataSourceTransactionManager = dataSourceTransactionManager;
    }

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

    @Override
    public Boolean save(DsRequestConfigAllDto dto) {
        DsRequestConfigDto requestConfigDto = dto.getDsRequestConfigDto();
        DsRequestConfig requestConfig = new DsRequestConfig();
        ObjectUtils.copyFields(requestConfigDto, requestConfig);

        TransactionStatus transaction = dataSourceTransactionManager.getTransaction(TransactionUtils.getTransactionDefinition());
        try{
            if (requestConfigDto.getId()==null){
                requestConfig.setCreateTime(new Date());
                UserBase ub = new UserBase();
                ub.setUsername(SecurityContextHolder.getContext().getAuthentication().getName());
                PageInfo<UserBase> userBaseInfo = userModuleService.getUserBaseInfo(ub, 1, 1);
                AssertUrils.state(userBaseInfo.getTotal()>0, new RuntimeException("创建用户未找到"));
                requestConfig.setCreateUserId(userBaseInfo.getList().get(0).getId());
            }
            saveOrUpdate(requestConfig);
            ObjectMapper om = new ObjectMapper();

            AuthPe authPe = dto.getAuthPe();
            if (authPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.AUTH);
                dpc.setConfigJson(om.writeValueAsString(authPe));
                dsRequestProcessConfigDao.saveOrUpdateByTypeAndConfigId(dpc);
            }

            InPe inPe = dto.getInPe();
            if (inPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.IN);
                dpc.setConfigJson(om.writeValueAsString(inPe));
                dsRequestProcessConfigDao.saveOrUpdateByTypeAndConfigId(dpc);
            }

            InJsPe inJsPe = dto.getInJsPe();
            if (inJsPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.IN_JS);
                dpc.setConfigJson(om.writeValueAsString(inJsPe));
                dsRequestProcessConfigDao.saveOrUpdateByTypeAndConfigId(dpc);
            }

            SqlPe sqlPe = dto.getSqlPe();
            if (sqlPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.SQL);
                dpc.setConfigJson(om.writeValueAsString(sqlPe));
                dsRequestProcessConfigDao.saveOrUpdateByTypeAndConfigId(dpc);
            }

            OutPe outPe = dto.getOutPe();
            if (outPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.OUT);
                dpc.setConfigJson(om.writeValueAsString(outPe));
                dsRequestProcessConfigDao.saveOrUpdateByTypeAndConfigId(dpc);
            }

            OutJsPe outJsPe = dto.getOutJsPe();
            if (outJsPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.OUT_JS);
                dpc.setConfigJson(om.writeValueAsString(outJsPe));
                dsRequestProcessConfigDao.saveOrUpdateByTypeAndConfigId(dpc);
            }

            OutDataPe outDataPe = dto.getOutDataPe();
            if (outDataPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.OUT_DATA);
                dpc.setConfigJson(om.writeValueAsString(outDataPe));
                dsRequestProcessConfigDao.saveOrUpdateByTypeAndConfigId(dpc);
            }
            dataSourceTransactionManager.commit(transaction);
        }catch (Exception exception){
            dataSourceTransactionManager.rollback(transaction);
        }

        return true;
    }

    @Override
    public DsRequestConfigAllDto getDetail(Integer id) {
        DsRequestConfigAllDto dto = new DsRequestConfigAllDto();
        DsRequestConfig config = this.getById(id);
        AssertUrils.state(config != null, DsRequestConfigNotFoundError.class);
        DsRequestConfigDto c = new DsRequestConfigDto();
        ObjectUtils.copyFields(config, c);
        dto.setDsRequestConfigDto(c);
        List<DsRequestProcessConfig> list = dsRequestProcessConfigService.lambdaQuery()
                .eq(DsRequestProcessConfig::getRequestConfigId, id)
                .list();
        ObjectMapper om = new ObjectMapper();
        list.forEach(e->{
            ThreadUtils.runIgnoreException(()->{
                switch(e.getType()){
                    case RequestProcessType.AUTH -> {
                        AuthPe authPe = om.readValue(e.getConfigJson(), AuthPe.class);
                        dto.setAuthPe(authPe);
                    }
                    case RequestProcessType.IN -> {
                        InPe inPe = om.readValue(e.getConfigJson(), InPe.class);
                        dto.setInPe(inPe);
                    }
                    case RequestProcessType.IN_JS -> {
                        InJsPe inJsPe = om.readValue(e.getConfigJson(), InJsPe.class);
                        dto.setInJsPe(inJsPe);
                    }
                    case RequestProcessType.SQL -> {
                        SqlPe sqlPe = om.readValue(e.getConfigJson(), SqlPe.class);
                        dto.setSqlPe(sqlPe);
                    }
                    case RequestProcessType.OUT -> {
                        OutPe outPe = om.readValue(e.getConfigJson(), OutPe.class);
                        dto.setOutPe(outPe);
                    }
                    case RequestProcessType.OUT_JS -> {
                        OutJsPe outJsPe = om.readValue(e.getConfigJson(), OutJsPe.class);
                        dto.setOutJsPe(outJsPe);
                    }
                    case RequestProcessType.OUT_DATA -> {
                        OutDataPe outDataPe = om.readValue(e.getConfigJson(), OutDataPe.class);
                        dto.setOutDataPe(outDataPe);
                    }
                }
            });
        });
        return dto;
    }
}

