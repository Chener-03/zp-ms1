package xyz.chener.zp.zpusermodule.entity.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UiRoutingDto {

    private String path;

    private String name;

    private String component;

    private String redirect;

    private Map<String,Object> meta;

    private List<UiRoutingDto> children = new ArrayList<>();


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    public List<UiRoutingDto> getChildren() {
        return children;
    }

    public void setChildren(List<UiRoutingDto> children) {
        this.children = children;
    }
}
