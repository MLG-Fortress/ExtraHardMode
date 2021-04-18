package com.extrahardmode.config;

import com.extrahardmode.service.config.ConfigNode;

/**
 * Created by Fuck Da Police on 2016/02/10.
 */
public class ModuleNode implements ConfigNode {
    /**
     * Path.
     */
    private final String path;

    /**
     * Comment to be written to the file
     */
    private final String[] comments;

    /**
     * Variable type.
     */
    private final VarType type;

    /**
     * Normal Constructor
     */
    private ModuleNode(String path, VarType type, String... comments) {
        this.path = path;
        this.type = type;
        this.comments = comments;
    }

    @Override
    public String getPath() {
        return baseNode() + "." + path;
    }


    @Override
    public VarType getVarType() {
        return type;
    }


    @Override
    public Object getDefaultValue() {
        return true;
    }


    @Override
    public SubType getSubType() {
        return null;
    }


    @Override
    public Object getValueToDisable() {
        return false;
    }

    public static String baseNode() {
        return "Enabled Modules";
    }
}
