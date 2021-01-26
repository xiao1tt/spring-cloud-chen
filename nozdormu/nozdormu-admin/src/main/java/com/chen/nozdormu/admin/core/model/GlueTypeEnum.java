package com.chen.nozdormu.admin.core.model;

/**
 * @author chenxiaotong
 */
public enum GlueTypeEnum {
    /**
     *
     */
    BEAN("BEAN", false, null, null),
    ;

    private String desc;
    private boolean isScript;
    private String cmd;
    private String suffix;

    GlueTypeEnum(String desc, boolean isScript, String cmd, String suffix) {
        this.desc = desc;
        this.isScript = isScript;
        this.cmd = cmd;
        this.suffix = suffix;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isScript() {
        return isScript;
    }

    public String getCmd() {
        return cmd;
    }

    public String getSuffix() {
        return suffix;
    }

    public static GlueTypeEnum match(String name) {
        for (GlueTypeEnum item : GlueTypeEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return null;
    }
}