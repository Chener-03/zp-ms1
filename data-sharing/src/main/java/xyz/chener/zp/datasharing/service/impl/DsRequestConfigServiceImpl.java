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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.ObjectUtils;
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
import xyz.chener.zp.datasharing.error.config.BindDatasourceNotFount;
import xyz.chener.zp.datasharing.error.config.SqlRunError;
import xyz.chener.zp.datasharing.requestProcess.entity.RequestProcessType;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.*;
import xyz.chener.zp.datasharing.requestProcess.exec.SqlExec;
import xyz.chener.zp.datasharing.service.DsDatasourceService;
import xyz.chener.zp.datasharing.service.DsRequestConfigService;
import org.springframework.stereotype.Service;
import xyz.chener.zp.datasharing.service.DsRequestProcessConfigService;
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
            requestConfig.setCreateTime(new Date());
            saveOrUpdate(requestConfig);
            ObjectMapper om = new ObjectMapper();

            AuthPe authPe = dto.getAuthPe();
            if (authPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.AUTH);
                dpc.setConfigJson(om.writeValueAsString(authPe));
                dsRequestProcessConfigService.lambdaUpdate()
                        .eq(DsRequestProcessConfig::getRequestConfigId, requestConfig.getId())
                        .eq(DsRequestProcessConfig::getType, RequestProcessType.AUTH)
                        .update(dpc);
            }

            InPe inPe = dto.getInPe();
            if (inPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.IN);
                dpc.setConfigJson(om.writeValueAsString(inPe));
                dsRequestProcessConfigDao.saveOrUpdateByTypeAndConfigId(dpc);
                /*dsRequestProcessConfigService.lambdaUpdate()
                        .eq(DsRequestProcessConfig::getRequestConfigId, requestConfig.getId())
                        .eq(DsRequestProcessConfig::getType, RequestProcessType.IN)
                        .update(dpc);*/
            }

            InJsPe inJsPe = dto.getInJsPe();
            if (inJsPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.IN_JS);
                dpc.setConfigJson(om.writeValueAsString(inJsPe));
                dsRequestProcessConfigService.lambdaUpdate()
                        .eq(DsRequestProcessConfig::getRequestConfigId, requestConfig.getId())
                        .eq(DsRequestProcessConfig::getType, RequestProcessType.IN_JS)
                        .update(dpc);
            }

            SqlPe sqlPe = dto.getSqlPe();
            if (sqlPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.SQL);
                dpc.setConfigJson(om.writeValueAsString(sqlPe));
                dsRequestProcessConfigService.lambdaUpdate()
                        .eq(DsRequestProcessConfig::getRequestConfigId, requestConfig.getId())
                        .eq(DsRequestProcessConfig::getType, RequestProcessType.SQL)
                        .update(dpc);
            }

            OutPe outPe = dto.getOutPe();
            if (outPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.OUT);
                dpc.setConfigJson(om.writeValueAsString(outPe));
                dsRequestProcessConfigService.lambdaUpdate()
                        .eq(DsRequestProcessConfig::getRequestConfigId, requestConfig.getId())
                        .eq(DsRequestProcessConfig::getType, RequestProcessType.OUT)
                        .update(dpc);
            }

            OutJsPe outJsPe = dto.getOutJsPe();
            if (outJsPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.OUT_JS);
                dpc.setConfigJson(om.writeValueAsString(outJsPe));
                dsRequestProcessConfigService.lambdaUpdate()
                        .eq(DsRequestProcessConfig::getRequestConfigId, requestConfig.getId())
                        .eq(DsRequestProcessConfig::getType, RequestProcessType.OUT_JS)
                        .update(dpc);
            }

            OutDataPe outDataPe = dto.getOutDataPe();
            if (outDataPe != null){
                DsRequestProcessConfig dpc = new DsRequestProcessConfig();
                dpc.setRequestConfigId(requestConfig.getId());
                dpc.setType(RequestProcessType.OUT_DATA);
                dpc.setConfigJson(om.writeValueAsString(outDataPe));
                dsRequestProcessConfigService.lambdaUpdate()
                        .eq(DsRequestProcessConfig::getRequestConfigId, requestConfig.getId())
                        .eq(DsRequestProcessConfig::getType, RequestProcessType.OUT_DATA)
                        .update(dpc);
            }

            dataSourceTransactionManager.commit(transaction);
        }catch (Exception exception){
            dataSourceTransactionManager.rollback(transaction);
        }


        return true;
    }
}

