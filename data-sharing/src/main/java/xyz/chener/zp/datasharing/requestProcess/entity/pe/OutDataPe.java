package xyz.chener.zp.datasharing.requestProcess.entity.pe;


import lombok.Data;

import java.io.Serializable;

@Data
public class OutDataPe implements Serializable {

    private String type;

    public static class TYPE{
        public static final String JSON = "json";
        public static final String EXCEL = "excel";
        public static final String PDF = "pdf";
    }

}
