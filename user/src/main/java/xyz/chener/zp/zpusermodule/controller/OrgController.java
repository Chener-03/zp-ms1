package xyz.chener.zp.zpusermodule.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.chener.zp.common.config.UnifiedReturn;
import xyz.chener.zp.zpusermodule.entity.dto.OrgTreeDto;
import xyz.chener.zp.zpusermodule.service.OrgBaseService;

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


    public OrgController(OrgBaseService orgBaseService) {
        this.orgBaseService = orgBaseService;
    }

    @GetMapping("/getAllOrgTree")
    @PreAuthorize("hasAnyRole('org_list_query')")
    public List<OrgTreeDto> getAllOrgTree() {
        return orgBaseService.getOrgTree();
    }


}
