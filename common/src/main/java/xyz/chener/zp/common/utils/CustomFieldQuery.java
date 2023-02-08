package xyz.chener.zp.common.utils;

import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/02/08/16:27
 * @Email: chen@chener.xyz
 */
public class CustomFieldQuery {

    private Boolean isAll = true;

    private List<String> fields = new ArrayList<>();

    public Boolean getAll() {
        return isAll;
    }

    public void setAll(Boolean all) {
        isAll = all;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
