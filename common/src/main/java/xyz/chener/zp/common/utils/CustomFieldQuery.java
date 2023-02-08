package xyz.chener.zp.common.utils;

import org.w3c.dom.stylesheets.LinkStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/02/08/16:27
 * @Email: chen@chener.xyz
 */
public abstract class CustomFieldQuery {

    private static final ThreadLocal<List<String>> localVar = new ThreadLocal<>();

    public static class CustomFieldQueryCloseable implements AutoCloseable{
        @Override
        public void close() {
            localVar.remove();
        }
    }

    public static CustomFieldQueryCloseable StartQuery(CustomFieldQuery query,Class<?> entityClass){
        localVar.set(query.getFields());
        return new CustomFieldQueryCloseable();
    }

    public static CustomFieldQueryCloseable StartQuery(List<String> fields){
        localVar.set(fields);
        return new CustomFieldQueryCloseable();
    }

    public static List<String> getQuery()
    {
        List<String> list = localVar.get();
        if (list == null) {
            list = Collections.EMPTY_LIST;
        }
        localVar.remove();
        return list;
    }


    public CustomFieldQueryCloseable StartQuery() {
        return CustomFieldQuery.StartQuery(this,this.getClass());
    }

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
