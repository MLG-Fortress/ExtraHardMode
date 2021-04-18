package com.extrahardmode.task;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.config.messages.MsgCategory;
import com.extrahardmode.module.MsgModule;
import com.extrahardmode.module.PlayerModule;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WeightCheckTask implements Runnable
{
    private final ExtraHardMode mPlugin;
    private final RootConfig CFG;
    private final MsgModule mMessenger;
    private static HashMap<UUID, Long> mLastClicks = new HashMap<UUID, Long>();
    private static Lock lock = new ReentrantLock();


    public WeightCheckTask(ExtraHardMode plugin)
    {
        this.mPlugin = plugin;
        CFG = plugin.getModuleForClass(RootConfig.class);
        mMessenger = plugin.getModuleForClass(MsgModule.class);
    }


    @Override
    public void run()
    {
        Iterator<UUID> lastClicksIter = mLastClicks.keySet().iterator();
        while (lastClicksIter.hasNext())
        {
            UUID playerUuuid = lastClicksIter.next();
            Player player = mPlugin.getServer().getPlayer(playerUuuid);
            //Remove players that haven't clicked in their inventory for 5 seconds
            if (System.currentTimeMillis() - mLastClicks.get(playerUuuid) > 5000 || player == null)
            {
                lastClicksIter.remove();
                mMessenger.hidePopup(player, MsgCategory.WEIGHT_MSG.getUniqueIdentifier());
            } else
            {
                final double armorPoints = CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_ARMOR_POINTS, player.getWorld().getName());
                final double invPoints = CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_INV_POINTS, player.getWorld().getName());
                final double toolPoints = CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_TOOL_POINTS, player.getWorld().getName());
                final double maxPoints = CFG.getDouble(RootNode.NO_SWIMMING_IN_ARMOR_MAX_POINTS, player.getWorld().getName());

                final float weight = PlayerModule.inventoryWeight(player, (float) armorPoints, (float) invPoints, (float) toolPoints);

                List<String> weightMessage = new ArrayList<String>(2);
                weightMessage.add(String.format("Weight %.1f/%.1f", weight, maxPoints));
                weightMessage.add(weight > maxPoints ? ChatColor.RED + "U will drown" : ChatColor.GREEN + "U won't drown");
                mMessenger.sendPopup(player, MsgCategory.WEIGHT_MSG, weightMessage, false);
            }
        }
    }


    public static void updateLastCLick(UUID uuid)
    {
        mLastClicks.put(uuid, System.currentTimeMillis());
    }
}
