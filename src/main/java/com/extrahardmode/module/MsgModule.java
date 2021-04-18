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

package com.extrahardmode.module;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.messages.MessageConfig;
import com.extrahardmode.config.messages.MessageNode;
import com.extrahardmode.config.messages.MsgCategory;
import com.extrahardmode.service.EHMModule;
import com.extrahardmode.service.FindAndReplace;
import com.extrahardmode.service.PermissionNode;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.diemex.scoreboardnotifier.NotificationManager;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Calendar;
import java.util.List;

/**
 * @author Max
 */
public class MsgModule extends EHMModule
{
    private MessageConfig messages;
    private MsgPersistModule persistModule;

    private NotificationManager manager;

    private final Table<String, MessageNode, Long> timeouts = HashBasedTable.create();


    /**
     * Constructor
     */
    public MsgModule(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        messages = plugin.getModuleForClass(MessageConfig.class);
        persistModule = plugin.getModuleForClass(MsgPersistModule.class);
        manager = new NotificationManager(plugin);
        /*try
        {
            SBPopupAPI api = (SBPopupAPI) plugin.getServer().getPluginManager().getPlugin("SBPopupAPI");
            popupsEnabled = api != null;
            if (api != null)
                manager = api.getSBManager();
        } catch (Exception ignored)
        {
        }*/
    }


    @Override
    public void closing()
    {
        timeouts.clear();
    }


    private void send(Player player, MessageNode node, String message)
    {
        switch (messages.getCat(node))
        {
            case NOTIFICATION:
                if (player == null)
                {
                    plugin.getLogger().warning("Could not send the following message: " + message);
                } else
                {
                    // FEATURE: don't spam messages
                    PlayerData playerData = plugin.getModuleForClass(DataStoreModule.class).getPlayerData(player.getName());
                    long now = Calendar.getInstance().getTimeInMillis();

                    if (!node.equals(playerData.lastMessageSent) || now - playerData.lastMessageTimestamp > 30000)
                    {
                        if (popupsAreEnabled(MsgCategory.NOTIFICATION))
                            sendPopup(player, MsgCategory.NOTIFICATION, message);
                        else
                            player.sendMessage(message);
                        playerData.lastMessageSent = node;
                        playerData.lastMessageTimestamp = now;
                    }

                }
                break;
            case TUTORIAL:
                Validate.notNull(player);
                if (persistModule.getCountFor(node, player.getName()) < messages.getMsgCount(node))
                {
                    long now = Calendar.getInstance().getTimeInMillis();

                    if (!timeouts.contains(player.getName(), node) || now - timeouts.get(player.getName(), node) > 120000) //only if contains
                    {
                        timeouts.put(player.getName(), node, now);
                        String msgText = messages.getString(node);
                        if (manager != null && popupsAreEnabled(MsgCategory.TUTORIAL))
                            sendPopup(player, MsgCategory.TUTORIAL, msgText);
                        else
                            player.sendMessage(ChatColor.DARK_RED + plugin.getTag() + ChatColor.WHITE + " " + msgText);
                        persistModule.increment(node, player.getName());
                    }
                } else
                    timeouts.remove(player, message);
                break;
            case BROADCAST:
                plugin.getServer().broadcastMessage(message);
                break;
            case DISABLED:
                break;
            default:
                throw new UnsupportedOperationException(messages.getCat(node) + " not implemented");
        }
    }


    /**
     * Broacast a message to the whole server
     *
     * @param node    message to broadcast
     * @param replace replace these placeholders
     */
    public void broadcast(MessageNode node, FindAndReplace... replace)
    {
        send(null, node, replace);
    }


    /**
     * Send a message to a Player.
     *
     * @param player to send the message to
     * @param node   message, gets loaded from the config
     */
    public void send(Player player, MessageNode node)
    {
        //Don't send a message if node is empty/null
        if (messages.getString(node).isEmpty() || messages.getString(node) == null)
            return;
        send(player, node, messages.getString(node));
    }


    /**
     * Sends a message with variables which will be inserted in the specified areas
     *
     * @param player  Player to send the message to
     * @param message to send
     */
    public void send(Player player, MessageNode message, FindAndReplace... fars)
    {
        String msgText = messages.getString(message);
        for (FindAndReplace far : fars)
        {   /* Replace the placeholder with the actual value */
            msgText = far.replace(msgText);
        }
        send(player, message, msgText);
    }


    /**
     * Send the player an informative message to explain what he's doing wrong. Play an optional sound aswell
     * <p/>
     *
     * @param player     to send msg to
     * @param perm       permission to silence the message
     * @param sound      errorsound to play after the event got cancelled
     * @param soundPitch 20-35 is good
     */
    public void send(Player player, MessageNode node, PermissionNode perm, Sound sound, float soundPitch)
    {
        if (!player.hasPermission(perm.getNode()))
        {
            send(player, node, messages.getString(node));
            if (sound != null)
                player.playSound(player.getLocation(), sound, 1, soundPitch);
        }
    }


    /**
     * Send the player an informative message to explain what he's doing wrong.
     * <p/>
     *
     * @param player to send msg to
     * @param node   the message
     * @param perm   permission to silence the message
     */
    public void send(Player player, MessageNode node, PermissionNode perm)
    {
        send(player, node, perm, null, 0);
    }


    /**
     * Send a short message using SbPopupAPI
     *
     * @param player   player to send the message to
     * @param category type defines the length color for consistency
     * @param message  text to display
     */
    public void sendPopup(Player player, MsgCategory category, String message)
    {
        if (popupsAreEnabled(category))
        {
            int length;
            ChatColor titleColor;
            ChatColor textColor;
            String titleText = messages.getString(MessageNode.SB_MSG_TITLE);

            if (messages.getBoolean(MessageNode.SB_MSG_REMOVE_COLOR))
                message = ChatColor.stripColor(message);

            switch (category)
            {
                case BROADCAST:
                    length = messages.getInt(MessageNode.SB_MSG_BROADCAST_LEN);
                    titleColor = null;
                    textColor = messages.getColor(MessageNode.SB_MSG_BROADCAST_TEXT_CLR);
                    break;
                case ONE_TIME:
                case NOTIFICATION:
                    length = messages.getInt(MessageNode.SB_MSG_NOTIFICATION_LEN);
                    titleColor = null;
                    textColor = messages.getColor(MessageNode.SB_MSG_NOTIFICATION_TEXT_CLR);
                    break;
                case TUTORIAL:
                    length = messages.getInt(MessageNode.SB_MSG_TUTORIAL_LEN);
                    titleColor = null;
                    textColor = messages.getColor(MessageNode.SB_MSG_TUTORIAL_TEXT_CLR);
                    break;
                case DISABLED:
                default:
                    length = 0;
                    titleColor = null;
                    textColor = null;
            }

            manager.showPopup(player.getName(), category.getUniqueIdentifier(), length, titleColor, textColor, titleText, message);
        }
    }


    /**
     * Send a short message using SbPopupAPI
     *
     * @param player   player to send the message to
     * @param category type defines the length color for consistency
     * @param message  text already seperated into lines
     */
    public void sendPopup(Player player, MsgCategory category, List<String> message)
    {
        sendPopup(player, category, message, messages.getBoolean(MessageNode.SB_MSG_REMOVE_COLOR));
    }


    /**
     * Send a short message using SbPopupAPI
     *
     * @param player      player to send the message to
     * @param category    type defines the length color for consistency
     * @param message     text already seperated into lines
     * @param stripColors if colors should be removed from the message
     */
    public void sendPopup(Player player, MsgCategory category, List<String> message, boolean stripColors)
    {
        if (popupsAreEnabled(category))
        {
            int length;
            ChatColor titleColor;
            ChatColor textColor;
            String titleText = messages.getString(MessageNode.SB_MSG_TITLE);

            if (stripColors)
                for (int i = 0; i < message.size(); i++)
                    message.set(i, ChatColor.stripColor(message.get(i)));

            switch (category)
            {
                case BROADCAST:
                    length = messages.getInt(MessageNode.SB_MSG_BROADCAST_LEN);
                    titleColor = null;
                    textColor = messages.getColor(MessageNode.SB_MSG_BROADCAST_TEXT_CLR);
                    break;
                case ONE_TIME:
                case NOTIFICATION:
                    length = messages.getInt(MessageNode.SB_MSG_NOTIFICATION_LEN);
                    titleColor = null;
                    textColor = messages.getColor(MessageNode.SB_MSG_NOTIFICATION_TEXT_CLR);
                    break;
                case TUTORIAL:
                    length = messages.getInt(MessageNode.SB_MSG_TUTORIAL_LEN);
                    titleColor = null;
                    textColor = messages.getColor(MessageNode.SB_MSG_TUTORIAL_TEXT_CLR);
                    break;
                case DISABLED:
                default:
                    length = 0;
                    titleColor = null;
                    textColor = null;
            }
            manager.showPopup(player.getName(), category.getUniqueIdentifier(), length, titleColor, textColor, titleText, message);
        }
    }


    /**
     * Get the NotificationManager for direct sending of popups
     *
     * @return the manager used by extrahardmode
     */
    public NotificationManager getManager()
    {
        return manager;
    }


    /**
     * Hides/Removes the message with the given unqiue identifier
     *
     * @param player     player for which to hide message
     * @param identifier uique identifier of this message
     */
    public void hidePopup(Player player, String identifier)
    {
        if (manager != null && player != null)
            manager.removePopup(player.getName(), identifier);
    }


    /**
     * Are popops enabled
     *
     * @return if popupmanager is loaded
     */
    public boolean popupsAreEnabled(MsgCategory category)
    {
        if (messages.getBoolean(MessageNode.SB_MSG_ENABLE))
            switch (category.getSubcategory() != null ? category.getSubcategory() : category) //Some messages might inherit from another MsgCategory
            {
                case TUTORIAL:
                    return messages.getBoolean(MessageNode.SB_MSG_TUTORIAL);
                case NOTIFICATION:
                    return messages.getBoolean(MessageNode.SB_MSG_NOTIFICATION);
                case BROADCAST:
                    return messages.getBoolean(MessageNode.SB_MSG_BROADCAST);
                default:
                    return false;
            }
        return false;
    }
}
