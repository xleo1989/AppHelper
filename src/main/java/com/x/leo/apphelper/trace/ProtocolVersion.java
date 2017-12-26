package com.x.leo.apphelper.trace;

/**
 * Created by Miaoke on 13/04/2017.
 */

public enum ProtocolVersion {
    V_1_0("1.0"),CURRENT_VERSON("1.0");
    private String customName;
    private ProtocolVersion(String name){
        customName = name;
    }
    public String getCustomName(){
        return customName;
    }
}