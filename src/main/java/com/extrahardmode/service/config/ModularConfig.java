/*
 * This file is part of
 * ExtraHardMode Server Plugin for Minecraft
 *
 * Copyright (C) 2012 Ryan Hamshire
 * Copyright (C) 2013 Diemex
 *
 * ExtraHardMode is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ExtraHardMode is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero Public License
 * along with ExtraHardMode.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

package com.extrahardmode.service.config;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.service.EHMModule;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Modular configuration class that utilizes a ConfigNode enumeration as easy access and storage of configuration option values.
 *
 * @author Mitsugaru
 */
@SuppressWarnings("SameParameterValue")
public abstract class ModularConfig extends EHMModule
{
    /**
     * Cache of options for the config.
     */
    protected final Map<ConfigNode, Object> OPTIONS = new ConcurrentHashMap<ConfigNode, Object>();


    /**
     * Constructor.
     *
     * @param plugin - plugin instance.
     */
    protected ModularConfig(ExtraHardMode plugin)
    {
        super(plugin);
    }


    /**
     * This updates a configuration option from the file.
     *
     * @param node - ConfigNode to update.
     */
    @SuppressWarnings("unchecked")
    protected void updateOption(final ConfigNode node, final ConfigurationSection config)
    {
        switch (node.getVarType())
        {
            case LIST:
            {
                List<String> list = config.getStringList(node.getPath());
                if (list == null)
                {
                    list = (List<String>) node.getDefaultValue();
                }
                OPTIONS.put(node, list);
                break;
            }
            case DOUBLE:
            {
                OPTIONS.put(node, config.getDouble(node.getPath(), (Double) node.getDefaultValue()));
                break;
            }
            case STRING:
            {
                OPTIONS.put(node, config.getString(node.getPath(), (String) node.getDefaultValue()));
                break;
            }
            case INTEGER:
            {
                OPTIONS.put(node, config.getInt(node.getPath(), (Integer) node.getDefaultValue()));
                break;
            }
            case BOOLEAN:
            {
                OPTIONS.put(node, config.getBoolean(node.getPath(), (Boolean) node.getDefaultValue()));
                break;
            }
            case COLOR:
            {
                ChatColor color = SpecialParsers.parseColor(config.getString(node.getPath(), node.getDefaultValue() instanceof String ? (String) node.getDefaultValue() : ""));
                OPTIONS.put(node, color != null ? color : "NONE");
                break;
            }
            default:
            {
                OPTIONS.put(node, config.get(node.getPath(), node.getDefaultValue()));
            }
        }
    }


    /**
     * Saves the config.
     */
    public abstract void save();


    /**
     * Force set the value for the given configuration node.
     * <p/>
     * Note, there is no type checking with this method.
     *
     * @param node  - ConfigNode path to use.
     * @param value - Value to use.
     */
    public void set(final ConfigNode node, final Object value)
    {
        set(node.getPath(), value);
    }


    /**
     * Set the given path for the given value.
     *
     * @param path  - Path to use.
     * @param value - Value to use.
     */
    protected abstract void set(final String path, final Object value);


    /**
     * Get the integer value of the node.
     *
     * @param node - Node to use.
     * @return Value of the node. Returns -1 if unknown.
     */
    public int getInt(final ConfigNode node)
    {
        int i = -1;
        switch (node.getVarType())
        {
            case INTEGER:
            {
                try
                {
                    i = (Integer) OPTIONS.get(node);
                } catch (NullPointerException npe)
                {
                    i = (Integer) node.getDefaultValue();
                }
                break;
            }
            default:
            {
                throw new IllegalArgumentException("Attempted to get " + node.toString() + " of type " + node.getVarType() + " as an integer.");
            }
        }
        return i;
    }


    /**
     * Get the string value of the node.
     *
     * @param node - Node to use.
     * @return Value of the node. Returns and empty string if unknown.
     */
    protected String getString(final ConfigNode node)
    {
        String out = "";
        switch (node.getVarType())
        {
            case STRING:
            {
                out = (String) OPTIONS.get(node);
                if (out == null)
                {
                    out = (String) node.getDefaultValue();
                }
                break;
            }
            default:
            {
                throw new IllegalArgumentException("Attempted to get " + node.toString() + " of type " + node.getVarType() + " as a string.");
            }
        }
        return out;
    }


    /**
     * Get the list value of the node.
     *
     * @param node - Node to use.
     * @return Value of the node. Returns an empty list if unknown.
     */
    @SuppressWarnings("unchecked")
    public List<String> getStringList(final ConfigNode node)
    {
        List<String> list = new ArrayList<String>();
        switch (node.getVarType())
        {
            case LIST:
            {
                final ConfigurationSection config = plugin.getConfig();
                list = config.getStringList(node.getPath());
                if (list == null)
                {
                    list = (List<String>) node.getDefaultValue();
                }
                break;
            }
            default:
            {
                throw new IllegalArgumentException("Attempted to get " + node.toString() + " of type " + node.getVarType() + " as a List<String>.");
            }
        }
        return list;
    }


    /**
     * Get the double value of the node.
     *
     * @param node - Node to use.
     * @return Value of the node. Returns 0 if unknown.
     */
    public double getDouble(final ConfigNode node)
    {
        double d = 0.0;
        switch (node.getVarType())
        {
            case DOUBLE:
            {
                try
                {
                    d = (Double) OPTIONS.get(node);
                } catch (NullPointerException npe)
                {
                    d = (Double) node.getDefaultValue();
                }
                break;
            }
            default:
            {
                throw new IllegalArgumentException("Attempted to get " + node.toString() + " of type " + node.getVarType() + " as a double.");
            }
        }
        return d;
    }


    /**
     * Get the boolean value of the node.
     *
     * @param node - Node to use.
     * @return Value of the node. Returns false if unknown.
     */
    public boolean getBoolean(final ConfigNode node)
    {
        boolean bool = false;
        switch (node.getVarType())
        {
            case BOOLEAN:
            {
                bool = (Boolean) OPTIONS.get(node);
                break;
            }
            default:
            {
                throw new IllegalArgumentException("Attempted to get " + node.toString() + " of type " + node.getVarType() + " as a boolean.");
            }
        }
        return bool;
    }


    /**
     * Get the (chat)color value of the node.
     *
     * @param node - Node to use.
     * @return Value of the node. Returns null if no color set.
     */
    public ChatColor getColor(final ConfigNode node)
    {
        ChatColor color;
        switch (node.getVarType())
        {
            case COLOR:
            {
                Object value = OPTIONS.get(node);
                if (value instanceof ChatColor)
                    color = (ChatColor) value;
                else //ConcurrentHashMap doesn't allow null values, so we just put an object of another type in the map to symbolize a null value
                    color = null;
                break;
            }
            default:
            {
                throw new IllegalArgumentException("Attempted to get " + node.toString() + " of type " + node.getVarType() + " as a color.");
            }
        }
        return color;
    }


    /**
     * Reloads info from yaml file(s).
     */
    public abstract void reload();

    /**
     * Update settings that can be changed on the fly.
     *
     * @param config - Main config to load from.
     */
    public abstract void loadSettings(final ConfigurationSection config);

    /**
     * Load defaults.
     *
     * @param config - Main config to load to.
     */
    public abstract void loadDefaults(final ConfigurationSection config);

    /**
     * Check the bounds on the parameters to make sure that all config variables are legal and usable by the plugin.
     */
    public abstract void boundsCheck();

}