package com.toms.order.center.entity;

import java.util.Date;
import java.io.Serializable;

/**
 * 统一用户中心表(UnifyCenterAccount)实体类
 *
 * @author chenxiaotong
 * @since 2020-12-15 19:44:05
 */
public class UnifyCenterAccount implements Serializable {
    private static final long serialVersionUID = 986734151345625749L;
    /**
    * 自增主键
    */
    private Integer id;
    /**
    * 用户ID , 唯一索引
    */
    private String uid;
    /**
    * 账户
    */
    private String account;
    /**
    * 密码
    */
    private String password;
    /**
    * 用户名称
    */
    private String name;
    /**
    * 用户头像
    */
    private String portrait;
    /**
    * 来源所属设备
    */
    private String appKey;
    /**
    * 用户来源
    */
    private String channelType;
    /**
    * 用户来源
    */
    private String requestSource;
    /**
    * 用户状态
    */
    private String status;
    /**
    * 创建时间
    */
    private Date createTime;
    /**
    * 修改时间
    */
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getRequestSource() {
        return requestSource;
    }

    public void setRequestSource(String requestSource) {
        this.requestSource = requestSource;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

}