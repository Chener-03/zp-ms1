package xyz.chener.zp.datasharing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import xyz.chener.zp.common.config.query.QueryHelper;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.ObjectUtils;
import xyz.chener.zp.datasharing.connect.DBConnector;
import xyz.chener.zp.datasharing.connect.error.ConnectError;
import xyz.chener.zp.datasharing.dao.DsDatasourceDao;
import xyz.chener.zp.datasharing.entity.DsDatasource;
import xyz.chener.zp.datasharing.entity.dto.ConnectResult;
import xyz.chener.zp.datasharing.entity.dto.DsDatasourceDto;
import xyz.chener.zp.datasharing.entity.thirdparty.OrgBase;
import xyz.chener.zp.datasharing.entity.thirdparty.UserBase;
import xyz.chener.zp.datasharing.error.datasource.SaveDatasourceError;
import xyz.chener.zp.datasharing.service.DsDatasourceService;
import org.springframework.stereotype.Service;
import xyz.chener.zp.datasharing.service.UserModuleService;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * (DsDatasource)表服务实现类
 *
 * @author makejava
 * @since 2023-03-31 22:51:42
 */
@Service
public class DsDatasourceServiceImpl extends ServiceImpl<DsDatasourceDao, DsDatasource> implements DsDatasourceService {

    private final UserModuleService userModuleService;

    public DsDatasourceServiceImpl(@Qualifier("xyz.chener.zp.datasharing.service.UserModuleService") UserModuleService userModuleService) {
        this.userModuleService = userModuleService;
    }


    @Override
    public ConnectResult testConnection(DsDatasource dsDatasource) {
        if (dsDatasource.getId() != null && (dsDatasource.getPassword().matches("^[*]+$") || dsDatasource.getUsername().matches("^[*]+$"))){
            DsDatasource old = this.getById(dsDatasource.getId());
            if (old != null){
                dsDatasource.setPassword(old.getPassword());
                dsDatasource.setUsername(old.getUsername());
            }
        }
        ConnectResult res = new ConnectResult();
        DBConnector connector = DBConnector.chooseConnector(dsDatasource.getType());
        try {
            connector.testConnection(dsDatasource.getHost(), dsDatasource.getPort(), dsDatasource.getParamUrl()
                    , dsDatasource.getDatabaseName(), dsDatasource.getUsername(), dsDatasource.getPassword());
            res.setSuccess(true);
        }catch (ConnectError error){
            res.setSuccess(false);
            Throwable throwable = error.getThrowable();
            res.setMessage(String.format("连接失败，错误信息：%s,%s", throwable.getMessage(),throwable.getCause()));
        }
        return res;
    }

    @Override
    public DsDatasource saveOrUpdateDataSource(DsDatasource dsDatasource) {
        UserBase userBase = ObjectUtils.EntityChainWrapper.builder(UserBase.class)
                .set(UserBase::getUsername, SecurityContextHolder.getContext().getAuthentication().getName())
                .build();
        PageInfo<UserBase> userBaseInfo = userModuleService.getUserBaseInfo(userBase, 1, 1);
        AssertUrils.state(userBaseInfo!=null && userBaseInfo.getList().size() == 1, new SaveDatasourceError("操作用户未找到"));
        if (dsDatasource.getId() == null) {
            dsDatasource.setCreateUserId(userBaseInfo.getList().get(0).getId());
            dsDatasource.setCreateTime(new Date());
            this.save(dsDatasource);
        }else {
            this.updateById(dsDatasource);
        }
        return dsDatasource;
    }

    @Override
    public PageInfo<DsDatasourceDto> getList(DsDatasourceDto params, FieldQuery fieldQuery, Integer page, Integer size) {
        List<OrgBase> userOrgs = userModuleService.getUserOrgs(SecurityContextHolder.getContext().getAuthentication().getName());
        if (userOrgs == null || userOrgs.size() == 0) {
            return new PageInfo<>(Collections.emptyList());
        }
        QueryHelper.StartQuery(fieldQuery,DsDatasourceDto.class);
        PageHelper.startPage(page, size);
        List<DsDatasourceDto> list = getBaseMapper().getList(params, userOrgs.stream().map(e -> String.valueOf(e.getId())).toList());
        return new PageInfo<>(list);
    }


}

