package xyz.chener.zp.datasharing.requestProcess.entity.pe;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class InPe implements Serializable {

    private List<InItem> inItems = new ArrayList<>();

    @Data
    public static class InItem{

        private String verifyTypes;

        private String transformTypes;

        private String paramKey;

        private String value;

        private String defaultValue;

        private boolean must = false;
    }

    public static class VERIFY_TYPE{
        public static final String NUMBER = "NUMBER_VERIFY_TYPE";
        public static final String STRING = "STRING_VERIFY_TYPE";
        public static final String SQL_INJECT = "SQL_INJECT_VERIFY_TYPE";
        public static final String REGULAR = "REGULAR:";

    }

    public static class TRANSFORM_TYPE{
        public static final String BASE64DECODE = "BASE64DECODE";
    }


}
