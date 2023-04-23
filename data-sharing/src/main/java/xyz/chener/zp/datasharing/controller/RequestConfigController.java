package xyz.chener.zp.datasharing.controller;

import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.common.config.unifiedReturn.warper.FileWarper;
import xyz.chener.zp.common.entity.WriteList;
import xyz.chener.zp.datasharing.entity.dto.DsRequestConfigAllDto;
import xyz.chener.zp.datasharing.entity.dto.DsRequestConfigDto;
import xyz.chener.zp.datasharing.service.DsRequestConfigService;
import xyz.chener.zp.datasharing.service.DsRequestProcessConfigService;

import java.io.File;
import java.lang.ref.Reference;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: chenzp
 * @Date: 2023/04/06/16:50
 * @Email: chen@chener.xyz
 */

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web/requestConfig")
@Validated
public class RequestConfigController {

    private final DsRequestConfigService dsRequestConfigService;

    private final DsRequestProcessConfigService dsRequestProcessConfigService;


    public RequestConfigController(DsRequestConfigService dsRequestConfigService, DsRequestProcessConfigService dsRequestProcessConfigService) {
        this.dsRequestConfigService = dsRequestConfigService;
        this.dsRequestProcessConfigService = dsRequestProcessConfigService;
    }


    @GetMapping("/list")
    public PageInfo<DsRequestConfigDto> list(@ModelAttribute DsRequestConfigDto dto
        , @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size){
        return dsRequestConfigService.getDsRequestConfigList(dto,page,size);
    }


    @GetMapping("/getDetail")
    public DsRequestConfigAllDto getDetail(@RequestParam("id") Integer id){
        return dsRequestConfigService.getDetail(id);
    }


    @PostMapping("/getSqlResultParam")
    public List<String>[] getSqlResultParam(@RequestParam("sql") String sql,@RequestParam("datasourceId") Integer datasourceId){
        return dsRequestConfigService.getSqlResultParam(sql,datasourceId);
    }

    @PostMapping("/save")
    public Integer save(@RequestBody @Validated DsRequestConfigAllDto dto){
        return dsRequestConfigService.save(dto);
    }

    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable("id") Integer id){
        return dsRequestConfigService.delete(id);
    }

    @GetMapping("/getDocumentMD")
    public String getDocumentMD(@RequestParam("id") Integer id){
        return dsRequestConfigService.getDocumentMD(id);
    }

    @GetMapping("/getDocumentMDs")
    public String getDocumentMDs(@RequestParam("id") List<Integer> id){
        return dsRequestConfigService.getDocumentMDs(id);
    }

    @GetMapping("/getDocumentMDsFile")
    @WriteList
    public FileWarper getDocumentMDsFile(@RequestParam("id") List<Integer> id){
        String str = dsRequestConfigService.getDocumentMDs(id);
        return new FileWarper(str,"接口文档.md");
    }

}
