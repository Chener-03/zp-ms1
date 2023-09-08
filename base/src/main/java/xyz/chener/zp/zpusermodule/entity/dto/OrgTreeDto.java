package xyz.chener.zp.zpusermodule.entity.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import xyz.chener.zp.zpusermodule.entity.OrgBase;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/02/16/10:48
 * @Email: chen@chener.xyz
 */
public class OrgTreeDto {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    //中文简称
    private String label;


    private List<OrgTreeDto> children = new ArrayList<>();

    public OrgTreeDto  coverToThis(OrgBase orgBase) {
        this.setId(orgBase.getId());
        this.setLabel(orgBase.getOrgChSimpleName());
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<OrgTreeDto> getChildren() {
        return children;
    }

    public void setChildren(List<OrgTreeDto> children) {
        this.children = children;
    }
}
