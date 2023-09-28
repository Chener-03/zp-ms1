package xyz.chener.zp.common.entity;

public class PublicEnum {

    public static enum BOOL {
        TRUE(1, "是"),
        FALSE(0, "否");

        private final int code;
        private final String name;

        BOOL(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
    }

}
