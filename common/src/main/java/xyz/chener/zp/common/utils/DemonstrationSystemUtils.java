package xyz.chener.zp.common.utils;

import xyz.chener.zp.common.error.DemonstrationSystemError;

public class DemonstrationSystemUtils {

    private static final Boolean IS_DEMO = false;

    public static void ban() {
        if (IS_DEMO) {
            throw new DemonstrationSystemError();
        }
    }
}
