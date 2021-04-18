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

package com.extrahardmode.config;


import com.extrahardmode.service.IoHelper;
import com.extrahardmode.service.config.*;
import com.extrahardmode.service.config.customtypes.BlockRelationsList;
import com.extrahardmode.service.config.customtypes.PotionEffectHolder;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.*;

/**
 * A Wrapper that contains <ul> <li>a FileConfiguration</li> <li>A reference to the config file</li> <li>Information
 * about the Mode this Config is loaded in</li> <li>Information about which status this Config is in</li> <li></li></ul>
 */
public class EHMConfig
{
    /**
     * Nodes to load from the config
     */
    private Set<ConfigNode> mConfigNodes = new LinkedHashSet<ConfigNode>();

    /**
     * Loaded config values
     */
    private Map<ConfigNode, Object> mLoadedNodes = new HashMap<ConfigNode, Object>();

    /**
     * Location we loaded the File from
     */
    private File mConfigFile;

    /**
     * Loaded FileConfiguration
     */
    private FileConfiguration mConfig;

    /**
     * Header is at the top of the file
     */
    private Header mHeader;

    /**
     * Mode with which this config will get loaded
     */
    private Mode mMode = Mode.NOT_SET;

    /**
     * Worlds in which this config is active in
     */
    private Set<String> mWorlds = new LinkedHashSet<String>(); //Linked: keeps inserted order

    /**
     * If this config is enabled for all worlds
     */
    private boolean mEnabledForAll = false;

    /**
     * If the header on top the config should be printed
     */
    private boolean mPrintHeader = true;

    /**
     * If line comments should be printed
     */
    private boolean mPrintComments = true;

    /**
     * Some status information about this Config
     */
    private Status mStatus = Status.OK;

    /**
     * Node that holds the mode of the config (string)
     */
    private ConfigNode mModeNode = RootNode.MODE;

    /**
     * Node that holds the worlds of this config (list<string>)
     */
    private ConfigNode mWorldsNode = RootNode.WORLDS;

    /**
     * Node that determines if the header should be printed
     */
    private ConfigNode mPrintHeaderNode = RootNode.PRINT_HEADER;

    /**
     * Should node comments be printed
     */
    private ConfigNode mPrintCommentsNode = RootNode.PRINT_COMMENTS;


    /**
     * Constructor
     *
     * @param file configuration file
     */
    public EHMConfig(File file)
    {
        this.mConfigFile = file;
        this.mConfig = YamlConfiguration.loadConfiguration(mConfigFile);
    }


    /**
     * Constructor
     *
     * @param config       that's loaded
     * @param fullFilePath fileName including the directory!
     */
    public EHMConfig(FileConfiguration config, String fullFilePath)
    {
        this.mConfig = config;
        mConfigFile = new File(fullFilePath);
        if (!mConfigFile.exists())
        {
            try
            {
                mConfigFile.getParentFile().mkdirs();
                mConfigFile.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            Validate.isTrue(mConfigFile.exists() && mConfigFile.canWrite(), "FilePath " + fullFilePath + " doesn't exist or is not writable");
        }
    }


    /**
     * Constructor
     *
     * @param config that's loaded
     * @param file   File-Object to save to
     */
    public EHMConfig(FileConfiguration config, File file)
    {
        this.mConfig = config;
        mConfigFile = file;
    }


    /**
     * Initializes and fully loads this configuration
     * <pre>
     * - load mode
     * - load worlds
     * - load nodes
     * - verify nodes
     * </pre>
     */
    public void load()
    {
        if (mConfigNodes.isEmpty())
            throw new IllegalStateException("You forgot to add nodes to " + mConfigFile.getName());
        loadMode();
        loadCommentOptions();
        loadNodes();
        validateNodes();
        loadWorlds();
    }


    /**
     * Saves the config to file
     */
    public void save()
    {
        if (mLoadedNodes.isEmpty())
            throw new IllegalStateException("No nodes are loaded, nothing to save to " + mConfigFile.getName());
        saveNodes();
        if (mPrintHeader)
            writeHeader();
    }


    public Map<ConfigNode, Object> getLoadedNodes()
    {
        return mLoadedNodes;
    }


    /**
     * Get loaded FileConfiguration
     */
    public FileConfiguration getFileConfiguration()
    {
        return mConfig;
    }


    /**
     * Set FileConfiguration
     */
    public void setFileConfiguration(FileConfiguration config)
    {
        this.mConfig = config;
    }


    /**
     * Returns the File to save the FileConfiguration to
     */
    public File getConfigFile()
    {
        return mConfigFile;
    }


    /**
     * Get the fileName of this Config
     */
    public String getFileName()
    {
        return mConfigFile.getName();
    }


    /**
     * Set where to save this Config
     */
    public void setConfigFile(File configFile)
    {
        this.mConfigFile = configFile;
    }


    /**
     * Get the Mode this config should be loaded
     *
     * @return mode or Mode.NOT_SET if not set yet
     */
    public Mode getMode()
    {
        return mMode;
    }


    /**
     * Set the Mode with which this Config should be loaded
     *
     * @param mode to set
     */
    public void setMode(Mode mode)
    {
        this.mMode = mode;
    }


    /**
     * Get the Status of this config
     *
     * @return Status, initialized with Status.OK
     */
    public Status getStatus()
    {
        return mStatus;
    }


    /**
     * Set the Status of this Config
     *
     * @param status of the Config
     */
    public void setStatus(Status status)
    {
        this.mStatus = status;
    }


    /**
     * Changes the node from which to load the mode
     *
     * @param node node to set it to
     */
    public void setModeNode(ConfigNode node)
    {
        this.mModeNode = node;
    }


    /**
     * Changes the node from which to load the worlds
     *
     * @param node node to set it to
     */
    public void setWorldsNode(ConfigNode node)
    {
        this.mWorldsNode = node;
    }


    /**
     * Changes the node from which to load if we should print the header
     *
     * @param node node to set it to
     */
    public void setPrintHeaderNode(ConfigNode node)
    {
        this.mPrintHeaderNode = node;
    }


    /**
     * Changes the node from which to load if we should print line comments
     *
     * @param node node to set it to
     */

    public void setPrintCommentsNode(ConfigNode node)
    {
        this.mPrintCommentsNode = node;
    }


    /**
     * Register new nodes to load from the config
     *
     * @param nodes nodes to register
     */
    public void registerNodes(ConfigNode[] nodes)
    {
        Collections.addAll(mConfigNodes, nodes);
    }


    /**
     * Register new nodes to load from the config
     *
     * @param nodes nodes to register
     */
    public void registerNodes(Collection<ConfigNode> nodes)
    {
        mConfigNodes.addAll(nodes);
    }


    /**
     * Get the worlds for this config
     *
     * @return worlds in which the config is active
     */
    public Collection<String> getWorlds()
    {
        return mWorlds;
    }


    /**
     * Load the mode from the config
     */
    public void loadMode()
    {
        //DETERMINE MODE FIRST
        String modeString = mConfig.getString(mModeNode.getPath());
        try
        {
            if (modeString != null)
                setMode(Mode.valueOf(modeString.toUpperCase()));
        } catch (IllegalArgumentException ignored)
        {
        } finally
        {
            if (getMode() == null || getMode() == Mode.NOT_SET)
            {
                if (isMainConfig())
                    setMode(Mode.MAIN);
                else
                    setMode(Mode.INHERIT);
            }
        }
    }


    /**
     * Load the worlds where this config is active
     */
    public void loadWorlds()
    {
        mWorlds.addAll((List<String>)mLoadedNodes.get(RootNode.WORLDS));

        //Check for all worlds placeholder = Enables plugin for all worlds
        for (String world : mWorlds)
            if (world.equals(MultiWorldConfig.ALL_WORLDS))
                mEnabledForAll = true;
    }


    public void loadCommentOptions()
    {
        mPrintHeader = mConfig.getBoolean(mPrintHeaderNode.getPath(), true);
        mPrintComments = mConfig.getBoolean(mPrintCommentsNode.getPath(), true);
    }


    /**
     * Load all values from the config and save in our map
     */
    public void loadNodes()
    {
        loop:
        for (ConfigNode node : mConfigNodes)
        {
            Object obj = null;

            switch (node.getVarType())
            {
                case LIST:
                {
                    if (mConfig.get(node.getPath()) instanceof List)
                        obj = mConfig.getStringList(node.getPath());
                    break;
                }
                case DOUBLE:
                {
                    if (mConfig.get(node.getPath()) instanceof Double)
                        obj = mConfig.getDouble(node.getPath());
                    break;
                }
                case STRING:
                {
                    if (mConfig.get(node.getPath()) instanceof String)
                        obj = mConfig.getString(node.getPath());
                    break;
                }
                case INTEGER:
                {
                    if (mConfig.get(node.getPath()) instanceof Integer)
                        obj = mConfig.getInt(node.getPath());
                    break;
                }
                case BOOLEAN:
                {
                    if (mConfig.get(node.getPath()) instanceof Boolean)
                        obj = mConfig.getBoolean(node.getPath());
                    break;
                }
                case POTION_EFFECT:
                {
                    ConfigurationSection section = mConfig.getConfigurationSection(node.getPath());
                    obj = PotionEffectHolder.loadFromConfig(section);
                    break;
                }
                case MATERIAL:
                {
                    if (mConfig.getString(node.getPath()) != null)
                        obj = Material.matchMaterial(mConfig.getString(node.getPath()));
                    break;
                }
                case BLOCK_RELATION_LIST:
                {
                    if (mConfig.get(node.getPath()) instanceof List)
                    {
                        List<String> list = mConfig.getStringList(node.getPath());
                        BlockRelationsList blocks = new BlockRelationsList();
                        for (String str : list)
                            blocks.addFromConfig(str);
                        obj = blocks;
                    } else if (mConfig.isSet(node.getPath()))
                        obj = BlockRelationsList.EMPTY_LIST;
                    break;
                }
                //ignore comments
                case COMMENT:
                    continue loop;
                default:
                {
                    obj = mConfig.get(node.getPath());
                    throw new UnsupportedOperationException(node.getPath() + "No specific getter available for Type: " + " " + node.getVarType());
                }
            }
            mLoadedNodes.put(node, obj);
        }
    }


    /**
     * Make sure that all our loaded values are valid and usable by the plugin
     */
    public void validateNodes()
    {
        for (ConfigNode node : mConfigNodes)
        {
            switch (node.getVarType())
            {
                case INTEGER:
                {
                    Integer validated = Validation.validateInt(node, mLoadedNodes.get(node));
                    mLoadedNodes.put(node, validated);
                    break;
                }
                case COMMENT:
                    break;
                //TODO ADD BLOCKTYPE_LIST
                default:
                {
                    Object validateMe = mLoadedNodes.get(node);
                    if (validateMe == null)
                        mLoadedNodes.put(node, node.getDefaultValue());
                }
            }
            //Make sure the string of the node matches
            if (node == mModeNode)
                mLoadedNodes.put(node, mMode.name());
        }
    }


    /**
     * Writes all our validated objects back to the config in the order they were added
     */
    private void saveNodes()
    {
        FileConfiguration outConfig = new YamlConfiguration();
        for (ConfigNode node : mConfigNodes)
        {
            Object value = mLoadedNodes.get(node);
            //Custom writing code for our custom objects
            switch (node.getVarType())
            {
                case MATERIAL:
                {
                    if (value instanceof Material)
                    {
                        outConfig.set(node.getPath(), ((Material)value).name());
                        break;
                    }
                }
                case BLOCK_RELATION_LIST:
                {
                    if (value instanceof BlockRelationsList)
                    {
                        String[] blockStrings = ((BlockRelationsList) value).toConfigStrings();
                        outConfig.set(node.getPath(), blockStrings);
                        break;
                    }
                }
                case POTION_EFFECT:
                {
                    if (value instanceof PotionEffectHolder)
                    {
                        ((PotionEffectHolder) value).saveToConfig(outConfig, node.getPath());
                        break;
                    }
                }
                case COMMENT:
                    break;
                default:
                {
                    outConfig.set(node.getPath(), value);
                }
            }
        }
        try
        {
            outConfig.save(mConfigFile);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    private void writeHeader()
    {
        if (mHeader == null)
            return;
        ByteArrayOutputStream memHeaderStream = null;
        OutputStreamWriter memWriter = null;
        try
        {
            //Write Header to a temporary file
            memHeaderStream = new ByteArrayOutputStream();
            memWriter = new OutputStreamWriter(memHeaderStream, Charset.forName("UTF-8").newEncoder());
            memWriter.write(String.format(mHeader.toString()));
            memWriter.close();
            //Copy Header to the beginning of the config file
            IoHelper.writeHeader(mConfigFile, memHeaderStream);
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (memHeaderStream != null) memHeaderStream.close();
                if (memWriter != null) memWriter.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    public boolean isEnabledForAll()
    {
        return mEnabledForAll;
    }


    public boolean isMainConfig()
    {
        return mConfigFile.getName().equals("config.yml");
    }


    /**
     * Can this config be loaded, e.g. it has all the required nodes
     *
     * @return if the config can be loaded
     */
    public boolean isValid()
    {
        if (mConfig == null)
            throw new IllegalStateException("FileConfiguration hasn't been loaded yet");
        return (mConfig.getValues(true).containsKey(RootNode.baseNode()) && mConfig.getStringList(RootNode.WORLDS.getPath()) != null) || isMainConfig();
    }


    public void setHeader(Header header)
    {
        this.mHeader = header;
    }


    public boolean printHeader()
    {
        return mPrintHeader;
    }


    public boolean printComments()
    {
        return mPrintComments;
    }
}
