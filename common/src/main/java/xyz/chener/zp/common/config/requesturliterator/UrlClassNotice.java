package xyz.chener.zp.common.config.requesturliterator;

import java.util.List;

public interface UrlClassNotice {
    void notice(List<? extends Class<?>> urlClass,String contextPath);
}
