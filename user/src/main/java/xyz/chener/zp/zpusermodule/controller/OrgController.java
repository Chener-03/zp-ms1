package xyz.chener.zp.zpusermodule.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.opLog.annotation.OpLog;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.common.config.query.entity.FieldQuery;
import xyz.chener.zp.common.config.query.QueryHelper;
import xyz.chener.zp.common.utils.AssertUrils;
import xyz.chener.zp.common.utils.DemonstrationSystemUtils;
import xyz.chener.zp.zpusermodule.config.oplog.OpRecordMybatisWrapper;
import xyz.chener.zp.zpusermodule.config.oplog.entity.OpEnum;
import xyz.chener.zp.zpusermodule.entity.Dictionaries;
import xyz.chener.zp.zpusermodule.entity.DictionariesKeyEnum;
import xyz.chener.zp.zpusermodule.entity.OrgBase;
import xyz.chener.zp.zpusermodule.entity.dto.OrgExtendInfoDto;
import xyz.chener.zp.zpusermodule.entity.dto.OrgInfoDto;
import xyz.chener.zp.zpusermodule.entity.dto.OrgTreeDto;
import xyz.chener.zp.zpusermodule.entity.dto.OrgUserDto;
import xyz.chener.zp.zpusermodule.error.dic.NoSuchDictoriesError;
import xyz.chener.zp.zpusermodule.service.OrgBaseService;
import xyz.chener.zp.zpusermodule.service.impl.DictionariesServiceImpl;
import xyz.chener.zp.zpusermodule.service.impl.OrgUserMapServiceImpl;

import java.util.Collection;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/02/16/09:48
 * @Email: chen@chener.xyz
 */

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class OrgController {

    private final OrgBaseService orgBaseService;
    private final OrgUserMapServiceImpl orgUserMapService;
    private final DictionariesServiceImpl dictionariesService;


    public OrgController(OrgBaseService orgBaseService, OrgUserMapServiceImpl orgUserMapService, DictionariesServiceImpl dictionariesService) {
        this.orgBaseService = orgBaseService;
        this.orgUserMapService = orgUserMapService;
        this.dictionariesService = dictionariesService;
    }

    @GetMapping("/getAllOrgTree")
    @PreAuthorize("hasAnyRole('org_list_query','org_list_query_only_sub')")
    public List<OrgTreeDto> getAllOrgTree() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long orgId = orgBaseService.getUserSelectOrgAuthorities(authorities,username);
        return orgBaseService.getOrgTree(orgId);
    }

    @GetMapping("/getOrgInfo")
    @PreAuthorize("hasAnyRole('org_list_query','org_list_query_only_sub')")
    public OrgInfoDto getOrgInfo(@RequestParam Long id ) {
        return orgBaseService.getOrgInfo(id);
    }

    @GetMapping("/getOrgBaseInfo")
    @PreAuthorize("hasAnyRole('org_list_query','org_list_query_only_sub')")
    public OrgBase getOrgBaseInfo(@RequestParam Long id,@ModelAttribute FieldQuery query) {
        QueryHelper.StartQuery(query, OrgBase.class);
        return orgBaseService.lambdaQuery().eq(OrgBase::getId,id).one();
    }


    @GetMapping("/getOrgExtendInfo")
    @PreAuthorize("hasAnyRole('org_list_query','org_list_query_only_sub')")
    public OrgExtendInfoDto getOrgExtendInfo(@RequestParam Long id) {
        return orgUserMapService.getOrgExtendInfo(id);
    }


    @GetMapping("/getOrgUsers")
    @PreAuthorize("hasAnyRole('org_list_query','org_list_query_only_sub')")
    public PageInfo<OrgUserDto> getOrgUsers(@RequestParam Long id
                , @RequestParam(defaultValue = "1") Integer page
                , @RequestParam(defaultValue = "10") Integer size)
    {
        return orgUserMapService.getOrgUsers(id,page,size);
    }

    @GetMapping("/getOrgTypesJson")
    @PreAuthorize("hasAnyRole('org_list_query','org_list_query_only_sub')")
    public String  getOrgTypesJson() {
        Dictionaries dic = dictionariesService.lambdaQuery().eq(Dictionaries::getId, DictionariesKeyEnum.ORG_TYPE).one();
        AssertUrils.state(dic != null, NoSuchDictoriesError.class);
        return dic.getValue0();
    }

    @PostMapping("/saveOrgInfo")
    @PreAuthorize("hasAnyRole('org_list_update','org_list_update_only_sub')")
    @OpLog(operateName = OpEnum.UPDATEORGINFO,recordClass = OpRecordMybatisWrapper.class )
    public OrgInfoDto saveOrgInfo(@ModelAttribute @Validated OrgInfoDto orgInfoDto) {
        DemonstrationSystemUtils.ban();
        if (orgBaseService.saveOrUpdateOrg(orgInfoDto)) {
            return orgInfoDto;
        } else {
            return null;
        }
    }

    @PostMapping("/deleteOrg")
    @PreAuthorize("hasAnyRole('org_list_update','org_list_update_only_sub')")
    @OpLog(operateName = OpEnum.DELETEORGINFO,recordClass = OpRecordMybatisWrapper.class )
    public Boolean deleteOrg(@RequestParam Long id) {
        DemonstrationSystemUtils.ban();
        return orgBaseService.deleteOrg(id);
    }

    @PostMapping("/disableOrg")
    @PreAuthorize("hasAnyRole('org_list_update','org_list_update_only_sub')")
    @OpLog(operateName = OpEnum.UPDATEORGINFO,recordClass = OpRecordMybatisWrapper.class )
    public Boolean disableOrg(@RequestParam Long id,@RequestParam Boolean disable) {
        DemonstrationSystemUtils.ban();
        return orgBaseService.lambdaUpdate().set(OrgBase::getDisable,disable).eq(OrgBase::getId,id).update();
    }


    @PostMapping("/addOrgUser")
    @PreAuthorize("hasAnyRole('org_list_update','org_list_update_only_sub')")
    @OpLog(operateName = OpEnum.UPDATEORGUSER,recordClass = OpRecordMybatisWrapper.class )
    public Boolean addOrgUser(@RequestParam Long orgId,@RequestParam List<Long> userIds) {
        DemonstrationSystemUtils.ban();
        return orgUserMapService.addOrgUser(orgId,userIds);
    }

    @PostMapping("/deleteOrgUser")
    @PreAuthorize("hasAnyRole('org_list_update','org_list_update_only_sub')")
    @OpLog(operateName = OpEnum.UPDATEORGUSER,recordClass = OpRecordMybatisWrapper.class )
    public Boolean deleteOrgUser(@RequestParam Long orgId,@RequestParam List<Long> userIds) {
        DemonstrationSystemUtils.ban();
        return orgUserMapService.deleteOrgUser(orgId,userIds);
    }


    @PostMapping("/flushOrgUserAuth")
    @PreAuthorize("hasAnyRole('org_list_update','org_list_update_only_sub')")
    public Boolean flushOrgUserAuth(@RequestParam Long orgId) {
        return orgUserMapService.flushOrgUserAuth(orgId);
    }

    @PostMapping("/disableOrgUserAuth")
    @PreAuthorize("hasAnyRole('org_list_update','org_list_update_only_sub')")
    public Boolean disableOrgUserAuth(@RequestParam Long orgId,@RequestParam Long userIds,@RequestParam Boolean disable) {
        DemonstrationSystemUtils.ban();
        return orgUserMapService.disableUserAuth(orgId,userIds,disable);
    }



    @GetMapping("/getUserOrgsByUsername")
    @PreAuthorize("hasAnyRole('microservice_call')")
    public List<OrgBase> getUserOrgs(@RequestParam("username") String username) {
        return orgBaseService.getUserOrgs(username);
    }

}
