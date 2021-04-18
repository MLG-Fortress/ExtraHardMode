package com.extrahardmode.command;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.service.ICommand;
import com.extrahardmode.service.PermissionNode;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Diemex
 */
public class EnabledCommand implements ICommand {
    @Override
    public boolean execute(ExtraHardMode plugin, CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission(PermissionNode.ADMIN.getNode())) {
            World world = null;
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length > 0) {
                    world = plugin.getServer().getWorld(args[0]);
                    sender.sendMessage(String.format("A world named %s doesn't exist", args[0]));
                }
                if (world == null)
                    world = player.getWorld();
            } else {
                if (args.length > 0)
                    world = plugin.getServer().getWorld(args[0]);
                if (world == null) {
                    sender.sendMessage(String.format("A world named %s doesn't exist", args[0]));
                    return false;
                }
            }
            RootConfig CFG = plugin.getModuleForClass(RootConfig.class);
            boolean enabled = Arrays.asList(CFG.getEnabledWorlds()).contains(world.getName());
            if (CFG.isEnabledForAll())
                sender.sendMessage(ChatColor.GREEN + "ExtraHardMode is enabled for all worlds");
            else {
                if (CFG.getEnabledWorlds().length > 0)
                    sender.sendMessage(String.format("%sEHM enabled for: %s", ChatColor.GREEN, Arrays.toString(CFG.getEnabledWorlds())));
                //All this is for disabled worlds
                List<String> enabledWorlds = Arrays.asList(CFG.getEnabledWorlds());
                List<String> disabledWorlds = new ArrayList<String>();
                for (World aWorld : plugin.getServer().getWorlds())
                    disabledWorlds.add(aWorld.getName());
                disabledWorlds.removeAll(enabledWorlds);
                if (disabledWorlds.size() > 0)
                    sender.sendMessage(String.format("%sEHM disabled for: %s", ChatColor.RED, disabledWorlds));
                sender.sendMessage(String.format("%sCurrent world: %s %s", enabled ? ChatColor.GREEN : ChatColor.RED, world.getName(), enabled ? "enabled" : "disabled"));
            }
        } else
            sender.sendMessage(ChatColor.RED + plugin.getTag() + " Lack permission: " + PermissionNode.ADMIN.getNode());
        return true;
    }
}
