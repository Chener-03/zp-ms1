package xyz.chener.zp.zpusermodule.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import xyz.chener.zp.common.utils.MapBuilder;
import xyz.chener.zp.zpusermodule.entity.dto.UiRoutingDto;

import java.util.Map;
import java.util.Optional;

/**
 * (UiRouting)表实体类
 *
 * @author makejava
 * @since 2023-01-19 22:55:16
 */
@SuppressWarnings("serial")
public class UiRouting extends Model<UiRouting> {

    @TableId(type = IdType.AUTO)
    private Integer id;
    //路径不可修改
    private String path;
    //名字不可修改
    private String name;
    //LAYOUT
    private String component;
    //不可修改
    private String redirect;
    //JsonObject->map,  保留 不存值
    private String meta;
    
    private Integer parentId;
    //该路由在菜单上展示的标题
    private String metaTitle;
    //该路由在菜单上展示的图标
    private String metaIcon;
    //决定该路由在菜单上是否默认展开
    private Boolean metaExpanded;
    //该路由在菜单上展示先后顺序，数字越小越靠前，默认为零
    private Integer metaOrderNo;
    //决定该路由是否在菜单上进行展示
    private Boolean metaHidden;
    //如果启用了面包屑，决定该路由是否在面包屑上进行展示
    private Boolean metaHiddenBreadcrumb;
    //内嵌 iframe 的地址
    private String metaFrameSrc;
    //内嵌 iframe 的地址是否以新窗口打开
    private String metaFrameBlank;


    public UiRouting coverToPo(UiRoutingDto routing)
    {
        this.path = routing.getPath();
        this.name = routing.getName();
        this.component = routing.getComponent();
        this.redirect = routing.getRedirect();

        Optional.ofNullable(routing.getMeta()).ifPresent(e->{
            this.metaTitle =  e.get("title") == null? null : e.get("title").toString();
            this.metaIcon =  e.get("icon") == null? null : e.get("icon").toString();
            this.metaExpanded =  e.get("expanded") == null? null : Boolean.parseBoolean(e.get("expanded").toString());
            this.metaOrderNo =  e.get("orderNo") == null? null : Integer.parseInt(e.get("orderNo").toString());
            this.metaHidden =  e.get("hidden") == null? null : Boolean.parseBoolean(e.get("hidden").toString());
            this.metaHiddenBreadcrumb =  e.get("hiddenBreadcrumb") == null? null : Boolean.parseBoolean(e.get("hiddenBreadcrumb").toString());
            this.metaFrameSrc =  e.get("frameSrc") == null? null : e.get("frameSrc").toString();
            this.metaFrameBlank =  e.get("frameBlank") == null? null : e.get("frameBlank").toString();
        });

        return this;

    }

    public UiRoutingDto coverToDto()
    {
        Map<String, Object> map = MapBuilder.<String, Object>getInstance()
                .add("title", this.metaTitle)
                .add("icon", this.metaIcon)
                .add("expanded", this.metaExpanded)
                .add("orderNo", this.metaOrderNo)
                .add("hidden", this.metaHidden)
                .add("hiddenBreadcrumb", this.metaHiddenBreadcrumb)
                .add("frameSrc", this.metaFrameSrc)
                .add("frameBlank", this.metaFrameBlank).build();
        UiRoutingDto dto = new UiRoutingDto();
        dto.setPath(this.path);
        dto.setName(this.name);
        dto.setComponent(this.component);
        dto.setRedirect(this.redirect);
        dto.setMeta(map);
        return dto;

    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaIcon() {
        return metaIcon;
    }

    public void setMetaIcon(String metaIcon) {
        this.metaIcon = metaIcon;
    }

    public Boolean getMetaExpanded() {
        return metaExpanded;
    }

    public void setMetaExpanded(Boolean metaExpanded) {
        this.metaExpanded = metaExpanded;
    }

    public Integer getMetaOrderNo() {
        return metaOrderNo;
    }

    public void setMetaOrderNo(Integer metaOrderNo) {
        this.metaOrderNo = metaOrderNo;
    }

    public Boolean getMetaHidden() {
        return metaHidden;
    }

    public void setMetaHidden(Boolean metaHidden) {
        this.metaHidden = metaHidden;
    }

    public Boolean getMetaHiddenBreadcrumb() {
        return metaHiddenBreadcrumb;
    }

    public void setMetaHiddenBreadcrumb(Boolean metaHiddenBreadcrumb) {
        this.metaHiddenBreadcrumb = metaHiddenBreadcrumb;
    }

    public String getMetaFrameSrc() {
        return metaFrameSrc;
    }

    public void setMetaFrameSrc(String metaFrameSrc) {
        this.metaFrameSrc = metaFrameSrc;
    }

    public String getMetaFrameBlank() {
        return metaFrameBlank;
    }

    public void setMetaFrameBlank(String metaFrameBlank) {
        this.metaFrameBlank = metaFrameBlank;
    }
}

