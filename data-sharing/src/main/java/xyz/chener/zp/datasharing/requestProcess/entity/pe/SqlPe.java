package xyz.chener.zp.datasharing.requestProcess.entity.pe;


import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SqlPe implements Serializable {

    public static final String TYPE_SELECT = "select";
    public static final String TYPE_UPDATE = "update";

    private List<SQL_ENTITY> sqls = new ArrayList<>();

    private Integer dataSourceId;



    @Data
    public static class SQL_ENTITY{
        private String sql;
        private String type;
    }

}
