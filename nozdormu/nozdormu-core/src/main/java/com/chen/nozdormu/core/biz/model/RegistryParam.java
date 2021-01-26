package com.chen.nozdormu.core.biz.model;

import java.io.Serializable;

/**
 * Created by xuxueli on 2017-05-10 20:22:42
 * @author chenxiaotong
 */
public class RegistryParam implements Serializable {

    private static final long serialVersionUID = 42L;

    private String registryGroup;
    private String registryKey;
    private String registryValue;
    private String registryJobs;

    public RegistryParam() {
    }

    public RegistryParam(String registryGroup, String registryKey, String registryValue) {
        this.registryGroup = registryGroup;
        this.registryKey = registryKey;
        this.registryValue = registryValue;
    }

    public RegistryParam(String registryGroup, String registryKey, String registryValue, String registryJobs) {
        this.registryGroup = registryGroup;
        this.registryKey = registryKey;
        this.registryValue = registryValue;
        this.registryJobs = registryJobs;
    }

    public String getRegistryGroup() {
        return registryGroup;
    }

    public void setRegistryGroup(String registryGroup) {
        this.registryGroup = registryGroup;
    }

    public String getRegistryKey() {
        return registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }

    public String getRegistryValue() {
        return registryValue;
    }

    public void setRegistryValue(String registryValue) {
        this.registryValue = registryValue;
    }

    public String getRegistryJobs() {
        return registryJobs;
    }

    public void setRegistryJobs(String registryJobs) {
        this.registryJobs = registryJobs;
    }

    @Override
    public String toString() {
        return "RegistryParam{" +
                "registryGroup='" + registryGroup + '\'' +
                ", registryKey='" + registryKey + '\'' +
                ", registryValue='" + registryValue + '\'' +
                ", registryJobs='" + registryJobs + '\'' +
                '}';
    }
}
