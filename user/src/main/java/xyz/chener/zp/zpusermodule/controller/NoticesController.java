package xyz.chener.zp.zpusermodule.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.chener.zp.common.config.UnifiedReturn;
import xyz.chener.zp.zpusermodule.entity.Dictionaries;
import xyz.chener.zp.zpusermodule.entity.DictionariesKeyEnum;
import xyz.chener.zp.zpusermodule.entity.dto.NoticesDto;
import xyz.chener.zp.zpusermodule.service.DictionariesService;
import xyz.chener.zp.zpusermodule.service.NoticesService;
import xyz.chener.zp.zpusermodule.service.impl.DictionariesServiceImpl;
import xyz.chener.zp.zpusermodule.service.impl.NoticesServiceImpl;

import java.util.List;

@RestController
@UnifiedReturn
@Slf4j
@RequestMapping("/api/web")
@Validated
public class NoticesController {

    private final DictionariesServiceImpl dictionariesService;
    private final NoticesServiceImpl noticesService;

    public NoticesController(DictionariesServiceImpl dictionariesService, NoticesServiceImpl noticesService) {
        this.dictionariesService = dictionariesService;
        this.noticesService = noticesService;
    }

    @GetMapping("/getNoticesList")
    public PageInfo<NoticesDto> getNoticesList(@ModelAttribute NoticesDto noticesDto
            ,@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size){
        PageHelper.startPage(page, size);
        List<NoticesDto> list = noticesService.getBaseMapper().getList(noticesDto);
        return new PageInfo<>(list);
    }


    @GetMapping("/getNoticesTypeJson")
    public String getNoticesTypeJson(){
        Dictionaries dictionaries = dictionariesService.lambdaQuery().eq(Dictionaries::getId, DictionariesKeyEnum.NOTICES_TYPE).one();
        return dictionaries.getValue0();
    }

}
