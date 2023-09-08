package xyz.chener.zp.zpusermodule.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.opLog.annotation.OpLog;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.common.config.query.QueryHelper;
import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.DemonstrationSystemUtils;
import xyz.chener.zp.zpusermodule.config.oplog.OpRecordMybatisWrapper;
import xyz.chener.zp.zpusermodule.config.oplog.entity.OpEnum;
import xyz.chener.zp.zpusermodule.entity.Permission;
import xyz.chener.zp.zpusermodule.entity.Role;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.dto.UiRoutingDto;
import xyz.chener.zp.zpusermodule.error.SqlRunException;
import xyz.chener.zp.zpusermodule.error.role.DefaultRoleDeleteError;
import xyz.chener.zp.zpusermodule.error.role.DefaultUserRoleDeleteError;
import xyz.chener.zp.zpusermodule.service.RoleService;
import xyz.chener.zp.zpusermodule.service.UserBaseService;
import xyz.chener.zp.zpusermodule.service.impl.PermissionServiceImpl;
import xyz.chener.zp.zpusermodule.service.impl.UiRoutingServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class PermissionController {

    private final RoleService roleService;
    private final PermissionServiceImpl permissionService;
    private final UiRoutingServiceImpl uiRoutingService;

    private final UserBaseService userBaseService;

    public PermissionController(RoleService roleService, PermissionServiceImpl permissionService, UiRoutingServiceImpl uiRoutingService, UserBaseService userBaseService) {
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.uiRoutingService = uiRoutingService;
        this.userBaseService = userBaseService;
    }

    @GetMapping("/getUserRole")
    @PreAuthorize("hasAnyRole('microservice_call','user_permission_list')")
    public PageInfo<Role> getUserRole(@ModelAttribute Role role
            , @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size)
    {
        try {
            PageHelper.startPage(page,size);
            return new PageInfo<>(roleService.lambdaQuery(role).list());
        }catch (Exception exception)
        {
            log.error(exception.getMessage());
            exception.printStackTrace();
            throw new SqlRunException(R.ErrorMessage.SQL_RUN_ERROR.get());
        }
    }


    @GetMapping("/getConcurrentUiRouting")
    public List<UiRoutingDto> getConcurrentUiRouting()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.nonNull(authentication))
            return uiRoutingService.getUserUiRouting(authentication.getName());
        return new ArrayList<>();
    }


    @GetMapping("/getPermissionListConst")
    @PreAuthorize("hasAnyRole('microservice_call','user_permission_list')")
    public List<Permission> getPermissionListConst()
    {
        return permissionService.lambdaQuery().orderBy(true,true,Permission::getPermissionEnName).list();

    }


    @GetMapping("/getRoleList")
    @PreAuthorize("hasAnyRole('microservice_call','user_permission_list')")
    public PageInfo<Role> getRoleList(@ModelAttribute Role role
            , @ModelAttribute FieldQuery query
            , @RequestParam(defaultValue = "1") Integer page
            , @RequestParam(defaultValue = "10") Integer size)
    {
        PageHelper.startPage(page,size);
        QueryHelper.StartQuery(query,Role.class);
        return new PageInfo<>(roleService.lambdaQuery(role).list());
    }


    @PostMapping("/saveRole")
    @PreAuthorize("hasAnyRole('microservice_call','user_permission_query')")
    @OpLog(operateName = OpEnum.UPDATEUSERROLE,recordClass = OpRecordMybatisWrapper.class )
    public Role saveRole(@RequestParam(required = false) Long id
            , @Length(max = 20,min = 3,message = "角色名长度3-20") @RequestParam String roleName
            , @RequestParam(required = false) List<String> roleList)
    {
//        AssertUrils.state(id == null || id > 1000 , DefaultRoleDeleteError.class);
        DemonstrationSystemUtils.ban();
        return roleService.saveOrUpdateRole(id,roleName,roleList);
    }


    @GetMapping("/getRoleUserCount")
    @PreAuthorize("hasAnyRole('microservice_call','user_permission_query')")
    public Long getRoleUserCount(@RequestParam(required = true) Long id)
    {
        try {
            return userBaseService.lambdaQuery().eq(UserBase::getRoleId,id).count();
        }catch (Exception exception){
            log.error(exception.getMessage());
            return 0L;
        }
    }


    @PostMapping("/deleteRole")
    @PreAuthorize("hasAnyRole('microservice_call','user_permission_query')")
    @OpLog(operateName = OpEnum.DALETEUSERROLE,recordClass = OpRecordMybatisWrapper.class )
    public void deleteRole(@RequestParam(required = true) Long id)
    {
        AssertUrils.state(id > 1000, DefaultRoleDeleteError.class);
        DemonstrationSystemUtils.ban();
        roleService.lambdaUpdate().eq(Role::getId,id).remove();
    }


    @PostMapping("/setUserRole")
    @PreAuthorize("hasAnyRole('microservice_call','user_permission_query')")
    public void setUserRole(@RequestParam(required = true) Long id
            , @RequestParam(required = false) Long roleId)
    {
        AssertUrils.state(id > 1000, DefaultUserRoleDeleteError.class);
        DemonstrationSystemUtils.ban();
        userBaseService.lambdaUpdate().eq(UserBase::getId,id).set(UserBase::getRoleId,roleId).update();
    }


    @PostMapping("/flushUiRouting")
    @PreAuthorize("hasAnyRole('microservice_call','user_permission_list')")
    public void flushUiRouting()
    {
        DemonstrationSystemUtils.ban();
        permissionService.flushUiPermission();
    }

}
