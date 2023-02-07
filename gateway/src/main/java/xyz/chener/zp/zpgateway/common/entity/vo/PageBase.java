package xyz.chener.zp.zpgateway.common.entity.vo;

import java.io.Serializable;
import java.util.List;


public class PageBase<T> implements Serializable {
    //总记录数
    protected long    total;
    //结果集
    protected List<T> list;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
