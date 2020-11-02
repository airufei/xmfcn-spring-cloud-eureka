package com.cn.xmf.job.admin.menu.model;

import com.cn.xmf.base.model.BaseEntitys;

import java.util.Date;
import java.math.BigDecimal;
import java.util.List;

/**
 * job-菜单Entity
 *
 * @author rufei.cn
 * @version 2018-10-10
 */
public class JobMenu extends BaseEntitys {

    private static final long serialVersionUID = 1L;
    private String name;        // 菜单名称

    private String url;        // 菜单地址

    private Integer isbutton;        // 是否button按钮 0不是 1是

    private Long fid;        // 父级菜单ID

    private Integer level;        // 菜单等级


    public JobMenu() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getIsbutton() {
        return isbutton;
    }

    public void setIsbutton(Integer isbutton) {
        this.isbutton = isbutton;
    }

    public Long getFid() {
        return fid;
    }

    public void setFid(Long fid) {
        this.fid = fid;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}