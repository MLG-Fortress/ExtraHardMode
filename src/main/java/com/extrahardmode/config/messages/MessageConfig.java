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

package com.extrahardmode.config.messages;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.service.IoHelper;
import com.extrahardmode.service.config.ConfigNode;
import com.extrahardmode.service.config.ModularConfig;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.Charset;
import java.util.logging.Level;

/** Configuration handler for the messages.yml file. */
public class MessageConfig extends ModularConfig
{
    /** File reference. */
    private final File file;

    /** Configuration object reference. */
    private final YamlConfiguration config;


    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    public MessageConfig(ExtraHardMode plugin)
    {
        super(plugin);
        file = new File(plugin.getDataFolder().getPath() + File.separator + "messages.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }


    @Override
    public void starting()
    {
        loadDefaults(config);
        reload();
        save();
    }


    @Override
    public void closing()
    {
        OPTIONS.clear();
        //reload();
        //save();
    }


    @Override
    public void save()
    {
        //Cleanly rewrite the config every time to remove comments and derp etc.
        try
        {
            FileConfiguration reorderedConfig = new YamlConfiguration();
            for (MessageNode node : MessageNode.values())
            {
                if (node.isCategoryNode()) //convert the parsed enum value to a string representation
                    reorderedConfig.set(node.getPath(), getCat(node).name().toLowerCase());
                else
                    switch (node.getVarType())
                    {
                        case COLOR:
                            if (getColor(node) == null)
                                reorderedConfig.set(node.getPath(), "NONE");
                            else
                                reorderedConfig.set(node.getPath(), getColor(node).name());
                            break;
                        default:
                            reorderedConfig.set(node.getPath(), OPTIONS.get(node));
                    }
            }
            reorderedConfig.save(file);
        } catch (IOException e)
        {
            plugin.getLogger().log(Level.SEVERE, "File I/O Exception on saving messages.yml", e);
        }
        setHeader(file);
    }


    /**
     * Set the header of the file before writing to it with bukkit yaml implementation
     *
     * @param file file to write the header to
     */
    private void setHeader(File file)
    {
        try
        {
            //Write header to a new file
            ByteArrayOutputStream memStream = new ByteArrayOutputStream();
            OutputStreamWriter memWriter = new OutputStreamWriter(memStream, Charset.forName("UTF-8").newEncoder());
            String[] header = {
                    "Messages sent by ExtraHardMode",
                    "Messages are only sent for modules that are activated",
                    "Modes (has to match exactly, ignores case)",
                    "Disabled: Message won't be sent even if feature that would sent the message is active",
                    "One_Time: Will be sent to every player only once",
                    "Notification: Gets sent every time with a timeout to prevent spamming chat",
                    "Tutorial: Sent a limited number of times and not displayed after 3 times",
                    "Broadcast: Shown to whole server. Only few messages make sense to be broadcasted",
                    "Variables:",
                    "$ALLCAPS is a variable and will be filled in for some messages",
                    "$PLAYER: Affected player",
                    "$PLAYERS: If multiple players are affected",
                    "$DEATH_MSG: Death message if someone dies",
                    "$ITEMS: a player lost"};
            StringBuilder sb = new StringBuilder();
            sb.append(StringUtils.repeat("#", 100));
            sb.append("%n");
            for (String line : header)
            {
                sb.append('#');
                sb.append(StringUtils.repeat(" ", 100 / 2 - line.length() / 2 - 1));
                sb.append(line);
                sb.append(StringUtils.repeat(" ", 100 / 2 - line.length() / 2 - 1 - line.length() % 2));
                sb.append('#');
                sb.append(String.format("%n"));
            }
            sb.append(StringUtils.repeat("#", 100));
            sb.append(String.format("%n"));
            //String.format: %n as platform independent line seperator
            memWriter.write(sb.toString());
            memWriter.close();

            IoHelper.writeHeader(file, memStream);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void set(String path, Object value)
    {
        config.set(path, value);
    }


    @Override
    public void reload()
    {
        // Reload config from file.
        try
        {
            if (!file.exists())
                file.createNewFile();
            config.load(file);
            loadSettings(config);
            boundsCheck();
        } catch (FileNotFoundException e)
        {
            plugin.getLogger().log(Level.SEVERE, "File messages.yml not found.", e);
        } catch (IOException e)
        {
            plugin.getLogger().log(Level.SEVERE, "File I/O Exception on saving messages.yml", e);
        } catch (InvalidConfigurationException e)
        {
            plugin.getLogger().log(Level.SEVERE, "Invalid configuration for messages.yml", e);
        }
    }


    @Override
    public void loadSettings(ConfigurationSection config)
    {
        for (MessageNode node : MessageNode.values())
        {
            if (node.isCategoryNode())
                updateCat(node, config);
            else
                updateOption(node, config);
        }
    }


    @Override
    public void loadDefaults(ConfigurationSection config)
    {
        for (MessageNode node : MessageNode.values())
        {
            if (!config.contains(node.getPath()))
            {
                config.set(node.getPath(), node.getDefaultValue());
            }
        }
    }


    @Override
    public String getString(ConfigNode node)
    {
        return ChatColor.translateAlternateColorCodes('&', super.getString(node));
    }


    private void updateCat(MessageNode node, ConfigurationSection config)
    {
        String val = config.getString(node.getPath());
        MsgCategory cat = null;
        try
        {
            cat = MsgCategory.valueOf(val != null ? val.toUpperCase() : "");
        } catch (IllegalArgumentException ignored)
        {
        } finally
        {
            if (cat == null)
                cat = node.getDefaultCategory();
            OPTIONS.put(node, cat);
        }
    }


    /**
     * Match the value of the node to the MsgCategory, also gets the mode of a given message if found
     *
     * @param node node to check
     *
     * @return the {@link com.extrahardmode.config.messages.MsgCategory} enum value of the node or null if the enum name of the node doesn't end with _MODE or value of the node is not found.
     */
    public MsgCategory getCat(MessageNode node)
    {
        MessageNode modeNode = null;
        Object obj = null;
        try
        {
            modeNode = MessageNode.valueOf(!node.name().endsWith("_MODE") ? node.name() + "_MODE" : node.name());
        } catch (IllegalArgumentException ignored)
        {
        } finally
        {
            //if (!node.name().endsWith("_MODE"))
            //Validate.notNull(modeNode, "There is no MODE node set for " + node.name());
            if (modeNode != null)
                obj = OPTIONS.get(modeNode);
        }
        return obj instanceof MsgCategory ? (MsgCategory) obj : null;
    }


    /**
     * Get how often a message is supposed to be displayed
     *
     * @return how often this message will be displayed. -1 = no limit
     */
    public int getMsgCount(MessageNode node)
    {
        switch (getCat(node))
        {
            case TUTORIAL:
                return 3;
            case NOTIFICATION:
                return -1;
            case BROADCAST:
                return -1;
            case ONE_TIME:
                return 1;
            default:
                throw new UnsupportedOperationException("Not Implemented MsgCategory");
        }
    }


    @Override
    public void boundsCheck()
    {
        for (ConfigNode node : MessageNode.values())
        {
            if (node.getSubType() == ConfigNode.SubType.PLAYER_NAME)
            {
                String nodeValue = config.getString(node.getPath());
                if (nodeValue != null)
                    set(node, nodeValue.length() > 16 ? nodeValue.substring(0, 16) : nodeValue);
                updateOption(node, config); //Kinda a quick workaround for the set() method in the super class not being implemented
            }
        }
    }
}