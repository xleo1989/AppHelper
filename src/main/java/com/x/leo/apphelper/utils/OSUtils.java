package com.x.leo.apphelper.utils;

import android.text.TextUtils;

import com.x.leo.apphelper.documented.DocumentMessage;
import com.x.leo.apphelper.log.xlog.XLog;

import java.io.IOException;

public class OSUtils {
    public static final String ROM_TYPE_KEY = "local_rom_type";
   
    //MIUI标识  
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";  
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";  
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";  
   
    //EMUI标识  
    private static final String KEY_EMUI_VERSION_CODE = "ro.build.version.emui";  
    private static final String KEY_EMUI_API_LEVEL = "ro.build.hw_emui_api_level";  
    private static final String KEY_EMUI_CONFIG_HW_SYS_VERSION = "ro.confg.hw_systemversion";  
   
    //Flyme标识  
    private static final String KEY_FLYME_ID_FALG_KEY = "ro.build.display.id";  
    private static final String KEY_FLYME_ID_FALG_VALUE_KEYWORD = "Flyme";  
    private static final String KEY_FLYME_ICON_FALG = "persist.sys.use.flyme.icon";  
    private static final String KEY_FLYME_SETUP_FALG = "ro.meizu.setupwizard.flyme";  
    private static final String KEY_FLYME_PUBLISH_FALG = "ro.flyme.published";  
   
    /** 
     * @param 
     * @return ROM_TYPE ROM类型的枚举 
     * @description获取ROM类型: MIUI_ROM, FLYME_ROM, EMUI_ROM, OTHER_ROM 
     */  
   
    public static ROM_TYPE getRomType() {
        DocumentMessage documentMessage = DocumentMessage.Companion.getDoc().setFileName("local_system.cfg");
        String message = documentMessage.getMessage(ROM_TYPE_KEY, null);
        documentMessage.reset();
        if (message != null) {
            try {
                return ROM_TYPE.valueOf(message);
            }catch (Exception e){
                XLog.INSTANCE.e("",e,100);
            }
        }

        ROM_TYPE rom_type = ROM_TYPE.OTHER;  
        try {
            BuildProperties buildProperties = BuildProperties.getInstance();
   
            if (buildProperties.containsKey(KEY_EMUI_VERSION_CODE) || buildProperties.containsKey(KEY_EMUI_API_LEVEL) || buildProperties.containsKey(KEY_MIUI_INTERNAL_STORAGE)) {
                DocumentMessage.Companion.getDoc().setFileName("local_system.cfg").putMessage(ROM_TYPE_KEY, ROM_TYPE.EMUI.name()).reset();
                return ROM_TYPE.EMUI;
            }  
            if (buildProperties.containsKey(KEY_MIUI_VERSION_CODE) || buildProperties.containsKey(KEY_MIUI_VERSION_NAME) || buildProperties.containsKey(KEY_MIUI_VERSION_NAME)) {
                DocumentMessage.Companion.getDoc().setFileName("local_system.cfg").putMessage(ROM_TYPE_KEY, ROM_TYPE.MIUI.name()).reset();
                return ROM_TYPE.MIUI;
            }  
            if (buildProperties.containsKey(KEY_FLYME_ICON_FALG) || buildProperties.containsKey(KEY_FLYME_SETUP_FALG) || buildProperties.containsKey(KEY_FLYME_PUBLISH_FALG)) {
                DocumentMessage.Companion.getDoc().setFileName("local_system.cfg").putMessage(ROM_TYPE_KEY, ROM_TYPE.FLYME.name()).reset();
                return ROM_TYPE.FLYME;
            }  
            if (buildProperties.containsKey(KEY_FLYME_ID_FALG_KEY)) {  
                String romName = buildProperties.getProperty(KEY_FLYME_ID_FALG_KEY);  
                if (!TextUtils.isEmpty(romName) && romName.contains(KEY_FLYME_ID_FALG_VALUE_KEYWORD)) {
                    DocumentMessage.Companion.getDoc().setFileName("local_system.cfg").putMessage(ROM_TYPE_KEY, ROM_TYPE.FLYME.name()).reset();
                    return ROM_TYPE.FLYME;
                }  
            }  
        } catch (IOException e) {
            e.printStackTrace();  
        }
        DocumentMessage.Companion.getDoc().setFileName("local_system.cfg").putMessage(ROM_TYPE_KEY, rom_type.name()).reset();
        return rom_type;  
    }
    public static String getRomInfo(){
        try {
            BuildProperties buildProperties = BuildProperties.getInstance();
            if (buildProperties != null && buildProperties.keySet().size() > 0) {
                StringBuilder sb = new StringBuilder();
                for (Object o : buildProperties.keySet()) {
                    if (o instanceof String) {
                        sb.append((String)o + ":" + buildProperties.getProperty((String)o) + "\n");
                    }
                }
                return sb.toString();
            }else
                return null;
        } catch (IOException e) {
            return null;
        }
    }
   
    public enum ROM_TYPE {  
        MIUI,  
        FLYME,  
        EMUI,  
        OTHER  
    }  
}  