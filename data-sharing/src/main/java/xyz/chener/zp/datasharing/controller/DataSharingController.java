package xyz.chener.zp.datasharing.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.chener.zp.common.config.unifiedReturn.annotation.UnifiedReturn;
import xyz.chener.zp.common.entity.WriteList;
import xyz.chener.zp.datasharing.service.DataSharingService;

@RestController
@Slf4j
@RequestMapping("/api/web/datasharing")
public class DataSharingController {

    private final DataSharingService dataSharingService;

    public DataSharingController(DataSharingService dataSharingService) {
        this.dataSharingService = dataSharingService;
    }


    @RequestMapping("/out/{requestId}")
    @WriteList
    public void sharing(@PathVariable("requestId") String requestId
            , HttpServletRequest request, HttpServletResponse response) {
        dataSharingService.sharing(requestId,false,request,response);
    }


}
