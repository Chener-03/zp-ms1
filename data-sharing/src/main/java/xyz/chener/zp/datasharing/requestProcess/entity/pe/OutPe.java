package xyz.chener.zp.datasharing.requestProcess.entity.pe;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
public class OutPe implements Serializable {
    private List<OutPe.OutItem> outItems = new ArrayList<>();

    @Data
    public static class OutItem{

        private String fomatterType;

        private String fomatterExp;

        private String transformTypes;

        private String paramKey;

        private String showKey;

        private String value;

        private String defaultValue;

    }

    public static class FORMAT_TYPE{
        public static final String TIME = "TIME";

    }


    public static class TRANSFORM_TYPE{
        public static final String BASE64ENCODE = "BASE64ENCODE";

        public static final String ANTIANAPHYLAXIS = "antianaphylaxis";

    }

}
