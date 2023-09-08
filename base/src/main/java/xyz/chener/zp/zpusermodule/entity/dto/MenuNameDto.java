package xyz.chener.zp.zpusermodule.entity.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/02/13/16:12
 * @Email: chen@chener.xyz
 */
public class MenuNameDto {

    private String label;

    private Integer id;

    private List<MenuNameDto> children = new ArrayList<>();

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MenuNameDto> getChildren() {
        return children;
    }

    public void setChildren(List<MenuNameDto> children) {
        this.children = children;
    }
}
