package com.extrahardmode.placeholder;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.module.DataStoreModule;
import com.extrahardmode.module.PlayerData;
import com.extrahardmode.module.PlayerModule;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Placeholder extends PlaceholderExpansion
{
    private ExtraHardMode plugin;
    private RootConfig CFG;


    public Placeholder(ExtraHardMode plugin)
    {
        this.plugin = plugin;
        this.CFG = plugin.getModuleForClass(RootConfig.class);
    }


    @Override
    public boolean persist()
    {
        return true;
    }


    @Override
    public boolean canRegister()
    {
        return true;
    }


    @Override
    public String getAuthor()
    {
        return plugin.getDescription().getAuthors().toString();
    }


    @Override
    public String getIdentifier()
    {
        return "extrahardmode";
    }


    @Override
    public String getVersion()
    {
        return plugin.getDescription().getVersion();
    }


    @Override
    public String onPlaceholderRequest(Player player, String identifier)
    {
        if (player == null)
        {
            return "";
        }

        if (identifier.equals("weight"))
        {
            PlayerData playerData = plugin.getModuleForClass(DataStoreModule.class).getPlayerData(player.getName());
            if (playerData.cachedWeightStatus <= 0)
            {
                World world = player.getWorld();
                float armorPoints = (float) CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_ARMOR_POINTS, world.getName());
                float inventoryPoints = (float) CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_INV_POINTS, world.getName());
                float toolPoints = (float) CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_TOOL_POINTS, world.getName());
                playerData.cachedWeightStatus = PlayerModule.inventoryWeight(player, armorPoints, inventoryPoints, toolPoints);
            }
            return String.format("%.1f", playerData.cachedWeightStatus);
        } else if (identifier.equals("max_weight"))
        {
            final double maxPoints = CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_MAX_POINTS, player.getWorld().getName());
            return String.format("%.1f", maxPoints);
        }
        return null;
    }
}
