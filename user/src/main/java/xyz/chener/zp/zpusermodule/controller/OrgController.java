package xyz.chener.zp.zpusermodule.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.chener.zp.common.config.UnifiedReturn;
import xyz.chener.zp.zpusermodule.entity.OrgUserMap;
import xyz.chener.zp.zpusermodule.entity.dto.OrgExtendInfoDto;
import xyz.chener.zp.zpusermodule.entity.dto.OrgInfoDto;
import xyz.chener.zp.zpusermodule.entity.dto.OrgTreeDto;
import xyz.chener.zp.zpusermodule.entity.dto.OrgUserDto;
import xyz.chener.zp.zpusermodule.service.OrgBaseService;
import xyz.chener.zp.zpusermodule.service.OrgUserMapService;
import xyz.chener.zp.zpusermodule.service.impl.OrgUserMapServiceImpl;

import java.text.DateFormat;
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


    public OrgController(OrgBaseService orgBaseService, OrgUserMapServiceImpl orgUserMapService) {
        this.orgBaseService = orgBaseService;
        this.orgUserMapService = orgUserMapService;
    }

    @GetMapping("/getAllOrgTree")
    @PreAuthorize("hasAnyRole('org_list_query','org_list_query_only_sub')")
    public List<OrgTreeDto> getAllOrgTree() {
        return orgBaseService.getOrgTree();
    }

    @GetMapping("/getOrgInfo")
    @PreAuthorize("hasAnyRole('org_list_query','org_list_query_only_sub')")
    public OrgInfoDto getOrgInfo(@RequestParam Integer id) {
        return orgBaseService.getOrgInfo(id);
    }


    @GetMapping("/getOrgExtendInfo")
    @PreAuthorize("hasAnyRole('org_list_query','org_list_query_only_sub')")
    public OrgExtendInfoDto getOrgExtendInfo(@RequestParam Integer id) {
        return orgUserMapService.getOrgExtendInfo(id);
    }


    @GetMapping("/getOrgUsers")
    @PreAuthorize("hasAnyRole('org_list_query','org_list_query_only_sub')")
    public PageInfo<OrgUserDto> getOrgUsers(@RequestParam Integer id
                , @RequestParam(defaultValue = "1") Integer page
                , @RequestParam(defaultValue = "10") Integer size)
    {
        return orgUserMapService.getOrgUsers(id,page,size);
    }


}
