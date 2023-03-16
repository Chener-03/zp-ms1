package xyz.chener.zp.system.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/03/16/17:04
 * @Email: chen@chener.xyz
 */
public class ServerNames {

    private Integer count;

    private List<String> doms = new ArrayList<>();

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getDoms() {
        return doms;
    }

    public void setDoms(List<String> doms) {
        this.doms = doms;
    }
}
