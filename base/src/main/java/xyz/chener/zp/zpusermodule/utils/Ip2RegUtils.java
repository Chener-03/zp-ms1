package xyz.chener.zp.zpusermodule.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @Author: chenzp
 * @Date: 2023/02/28/12:49
 * @Email: chen@chener.xyz
 */

@Slf4j
@Component
public class Ip2RegUtils {

    private Searcher searcher;

    public Ip2RegUtils() {
        try {
            InputStream stream = Ip2RegUtils.class.getResourceAsStream("/ip2reg/ip2region.xdb");
            if (stream != null){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                StreamUtils.copy(stream, baos);
                File file = new File("ip2region.xdb");
                FileOutputStream fs = new FileOutputStream(file);
                fs.write(baos.toByteArray());
                fs.close();
                byte[] data = Searcher.loadContentFromFile(file.getAbsolutePath());
                searcher = Searcher.newWithBuffer(data);
                stream.close();
            }
        }catch (Exception exception){
            log.error("ip2reg init error", exception);
        }
    }


    public static class Reg{
        private String country;
        private String region;
        private String province;
        private String city;
        private String isp;

        public void cover(String reg){
            String[] split = reg.split("\\|");
            if (split.length == 5){
                country = split[0];
                region = split[1];
                province = split[2];
                city = split[3];
                isp = split[4];
                country = country.equals("0") ? "" : country;
                region = region.equals("0") ? "" : region;
                province = province.equals("0") ? "" : province;
                city = city.equals("0") ? "" : city;
                isp = isp.equals("0") ? "" : isp;
            }
        }

        public String getCountry() {
            return country == null ? "" : country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getRegion() {
            return region== null ? "" : region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getProvince() {
            return province== null ? "" : province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city== null ? "" : city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getIsp() {
            return isp== null ? "" : isp;
        }

        public void setIsp(String isp) {
            this.isp = isp;
        }

        @Override
        public String toString() {
            return getCountry()+getRegion()+getProvince()+getCity()+getIsp();
        }
    }


    public Reg getReg(String ip){
        Reg reg = new Reg();
        try {
            reg.cover(searcher.search(ip));
        }catch (Exception ignored){ }
        return reg;
    }

}
