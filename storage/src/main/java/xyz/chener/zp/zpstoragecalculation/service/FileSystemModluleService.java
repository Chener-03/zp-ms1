package xyz.chener.zp.zpstoragecalculation.service;


import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "zp-storagecalculation-module")
public interface FileSystemModluleService {


    @RequestMapping(value = "/api/web/getLocalFiles")
    Response getLocalFile(@RequestParam("resourceUid") String resourceUid, @RequestParam("filename") String filename);

}
