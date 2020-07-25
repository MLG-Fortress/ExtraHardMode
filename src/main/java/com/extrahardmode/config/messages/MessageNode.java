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


import com.extrahardmode.service.config.ConfigNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Configuration nodes for messages.yml configuration file.
 */
public enum MessageNode implements ConfigNode
{
    /**
     * Display messages from extrahardmode in the scoreboard instead of spamming the chat?
     */
    SB_MSG_ENABLE("Display Messages In Scoreboard.Enable", VarType.BOOLEAN, true),
    /**
     * Display messages from extrahardmode in the scoreboard instead of spamming the chat?
     */
    SB_MSG_TITLE("Display Messages In Scoreboard.Scoreboard Title", VarType.STRING, SubType.PLAYER_NAME, "&cExtraHardMode"),
    /**
     * Display messages from extrahardmode in the scoreboard instead of spamming the chat?
     */
    SB_MSG_REMOVE_COLOR("Display Messages In Scoreboard.Remove All Color Codes", VarType.BOOLEAN, true),
    /**
     * Display messages from extrahardmode in the scoreboard instead of spamming the chat?
     */
    SB_MSG_NOTIFICATION("Display Messages In Scoreboard.Notification.Enable", VarType.BOOLEAN, true),
    /**
     * Display messages from extrahardmode in the scoreboard instead of spamming the chat?
     */
    SB_MSG_NOTIFICATION_LEN("Display Messages In Scoreboard.Notification.Displaytime In Ticks", VarType.INTEGER, 600),
    /**
     * Display messages from extrahardmode in the scoreboard instead of spamming the chat?
     */
    SB_MSG_NOTIFICATION_TEXT_CLR("Display Messages In Scoreboard.Notification.Textcolor", VarType.COLOR, "YELLOW"),

    /**
     * Display messages from extrahardmode in the scoreboard instead of spamming the chat?
     */
    SB_MSG_TUTORIAL("Display Messages In Scoreboard.Tutorial.Enable", VarType.BOOLEAN, true),
    /**
     * Display messages from extrahardmode in the scoreboard instead of spamming the chat?
     */
    SB_MSG_TUTORIAL_LEN("Display Messages In Scoreboard.Tutorial.Displaytime In Ticks", VarType.INTEGER, 1800),
    /**
     * Display messages from extrahardmode in the scoreboard instead of spamming the chat?
     */
    SB_MSG_TUTORIAL_TEXT_CLR("Display Messages In Scoreboard.Tutorial.Textcolor", VarType.COLOR, "RED"),

    /**
     * Display messages from extrahardmode in the scoreboard instead of spamming the chat?
     */
    SB_MSG_BROADCAST("Display Messages In Scoreboard.Broadcast.Enable", VarType.BOOLEAN, true),
    /**
     * Display messages from extrahardmode in the scoreboard instead of spamming the chat?
     */
    SB_MSG_BROADCAST_LEN("Display Messages In Scoreboard.Broadcast.Displaytime In Ticks", VarType.INTEGER, 1200),
    /**
     * Display messages from extrahardmode in the scoreboard instead of spamming the chat?
     */
    SB_MSG_BROADCAST_TEXT_CLR("Display Messages In Scoreboard.Broadcast.Textcolor", VarType.COLOR, "GREEN"),


    //Mode-Nodes have to be have the exact same name + _MODE
    NO_TORCHES_HERE_MODE
            ("NoTorchesHere.Mode", MsgCategory.NOTIFICATION),
    NO_TORCHES_HERE
            ("NoTorchesHere.Msg", MsgCategory.NOTIFICATION, "torches_underground",
                    "There's not enough air flow down here for permanent flames. Use another method to light your way."),
    STONE_MINING_HELP_MODE
            ("StoneMiningHelp.Mode", MsgCategory.NOTIFICATION),
    STONE_MINING_HELP
            ("StoneMiningHelp.Msg", MsgCategory.NOTIFICATION, "hardened_stone",
                    "You'll need an iron or diamond pickaxe to break stone.  Try exploring natural formations for exposed ore like coal, which softens stone around it when broken."),
    NO_PLACING_ORE_AGAINST_STONE_MODE
            ("NoPlacingOreAgainstStone.Mode", MsgCategory.NOTIFICATION),
    NO_PLACING_ORE_AGAINST_STONE
            ("NoPlacingOreAgainstStone.Msg", MsgCategory.NOTIFICATION, "hardened_stone_ore", "Sorry, you can't place ore next to stone."),
    REALISTIC_BUILDING_MODE
            ("RealisticBuilding.Mode", MsgCategory.NOTIFICATION),
    REALISTIC_BUILDING
            ("RealisticBuilding.Msg", MsgCategory.NOTIFICATION, "realistic_building", "You can't build while in the air."),
    REALISTIC_BUILDING_BENEATH_MODE
            ("RealisticBuildingBeneath.Mode", MsgCategory.NOTIFICATION),
    REALISTIC_BUILDING_BENEATH
            ("RealisticBuildingBeneath.Msg", MsgCategory.NOTIFICATION, "realistic_building_beneath",
                    "You can't place a block directly beneath you."),
    LIMITED_TORCH_PLACEMENTS_MODE
            ("LimitedTorchPlacements.Mode", MsgCategory.NOTIFICATION),
    LIMITED_TORCH_PLACEMENTS
            ("LimitedTorchPlacements.Msg", MsgCategory.NOTIFICATION, "torches_soft_blocks",
                    "It's too soft there to fasten a torch."),
    NO_CRAFTING_MELON_SEEDS_MODE
            ("NoCraftingMelonSeeds.Mode", MsgCategory.NOTIFICATION),
    NO_CRAFTING_MELON_SEEDS
            ("NoCraftingMelonSeeds.Msg", MsgCategory.NOTIFICATION, "no_crafting_melon_seeds",
                    "That appears to be seedless!"),
    LIMITED_END_BUILDING_MODE
            ("LimitedEndBuilding.Mode", MsgCategory.NOTIFICATION),
    LIMITED_END_BUILDING
            ("LimitedEndBuilding.Msg", MsgCategory.NOTIFICATION, "limited_building_end",
                    "Sorry, building here is very limited.  You may only break blocks to reach ground level."),
    DRAGON_FOUNTAIN_TIP_MODE
            ("DragonFountainTip.Mode", MsgCategory.NOTIFICATION),
    DRAGON_FOUNTAIN_TIP
            ("DragonFountainTip.Msg", MsgCategory.NOTIFICATION, "dragon_fountain_tip",
                    "Congratulations on defeating the dragon!  If you can't reach the fountain to jump into the portal, throw an ender pearl at it."),
    NO_SWIMMING_IN_ARMOR_MODE
            ("NoSwimmingInArmor.Mode", MsgCategory.NOTIFICATION),
    NO_SWIMMING_IN_ARMOR
            ("NoSwimmingInArmor.Msg", MsgCategory.NOTIFICATION, "no_swimming_in_armor_warning",
                    "You're carrying too much weight to swim!"),
    END_DRAGON_KILLED_MODE
            ("DragonDefeated.Mode", MsgCategory.BROADCAST),
    END_DRAGON_KILLED
            ("DragonDefeated.Msg", MsgCategory.BROADCAST, "end_dragon_killed",
                    "The dragon has been defeated!  ( By: " + Variables.PLAYERS.getVarName() + " )"),
    END_DRAGON_PLAYER_KILLED_MODE
            ("PlayerKilledByDragon.Mode", MsgCategory.BROADCAST),
    END_DRAGON_PLAYER_KILLED
            ("PlayerKilledByDragon.Msg", MsgCategory.BROADCAST, "player_killed_dragon",
                    Variables.PLAYER.getVarName() + " was killed while fighting the dragon!"),
    END_DRAGON_PLAYER_CHALLENGING_MODE
            ("PlayerChallengingDragon.Mode", MsgCategory.BROADCAST),
    END_DRAGON_PLAYER_CHALLENGING
            ("PlayerChallengingDragon.Msg", MsgCategory.BROADCAST, "player_challenging_dragon",
                    Variables.PLAYER.getVarName() + " is challenging the dragon!"),

    //Horses
    HORSE_FEED_LOW_MODE
            ("HorseFeedLow.Mode", MsgCategory.NOTIFICATION),
    HORSE_FEED_LOW
            ("HorseFeedLow.Msg", MsgCategory.NOTIFICATION, "horse_feed_low",
                    "You have saved your horse from starving, but it's still very hungry."),
    HORSE_FEED_MIDDLE_MODE
            ("HorseFeedMiddle.Mode", MsgCategory.NOTIFICATION),
    HORSE_FEED_MIDDLE
            ("HorseFeedMiddle.Msg", MsgCategory.NOTIFICATION, "horse_feed_middle",
                    "Your horse has ate well."),
    HORSE_FEED_HIGH_MODE
            ("HorseFeedHigh.Mode", MsgCategory.NOTIFICATION),
    HORSE_FEED_HIGH
            ("HorseFeedHigh.Msg", MsgCategory.NOTIFICATION, "horse_feed_high",
                    "Your horse is satiated."),

    HORSE_STARVING_LOW_MODE
            ("HorseStarveLow.Mode", MsgCategory.NOTIFICATION),
    HORSE_STARVING_LOW
            ("HorseStarveLow.Msg", MsgCategory.NOTIFICATION, "horse_starve_low",
                    "Your horse is very tired and needs some food."),
    HORSE_STARVING_DANGEROUS_MODE
            ("HorseStarveMiddle.Mode", MsgCategory.NOTIFICATION),
    HORSE_STARVING_DANGEROUS
            ("HorseStarveMiddle.Msg", MsgCategory.NOTIFICATION, "horse_starve_dangerous",
                    "Your horse is about to starve."),
    HORSE_STARVING_IMMINENT_DEATH_MODE
            ("HorseStarveHigh.Mode", MsgCategory.NOTIFICATION),
    HORSE_STARVING_IMMINENT_DEATH
            ("HorseStarveHigh.Msg", MsgCategory.NOTIFICATION, "horse_starve_imminent_death",
                    "Your horse is starving, feed it or it will die."),


    //Target Events
    CHARGED_CREEPER_TARGET_MODE
            ("ChargedCreeper.Mode", MsgCategory.TUTORIAL),
    CHARGED_CREEPER_TARGET
            ("ChargedCreeper.Msg", MsgCategory.TUTORIAL, "charged_creeper",
                    "Charged Creepers explode instantly when hit. Run!"),
    BLAZE_TARGET_NORMAL_MODE
            ("BlazeOverworld.Mode", MsgCategory.TUTORIAL),
    BLAZE_TARGET_NORMAL
            ("BlazeOverworld.Msg", MsgCategory.TUTORIAL, "blaze_overworld",
                    "Blazes spawn near lava and their fiery breath causes a big explosion on death!"),
    BLAZE_TARGET_NETHER_MODE
            ("BlazeNether.Mode", MsgCategory.TUTORIAL),
    BLAZE_TARGET_NETHER
            ("BlazeNether.Msg", MsgCategory.TUTORIAL, "blaze_nether",
                    "Blazes spawn everywhere in the Nether and may split on death!"),
    MAGMACUBE_TARGET_MODE
            ("Magmacube.Mode", MsgCategory.TUTORIAL),
    MAGMACUBE_TARGET
            ("Magmacube.Msg", MsgCategory.TUTORIAL, "magmacube",
                    "These small buggers actually are just disguised blazes!"),
    GHAST_TARGET_MODE
            ("Ghast.Mode", MsgCategory.TUTORIAL),
    GHAST_TARGET
            ("Ghast.Msg", MsgCategory.TUTORIAL, "ghast_warning",
                    "These fearsome Ghasts wear invisible arrow deflective armor! Ghasts drop a lot more loot as well."),
    PIGZOMBIE_TARGET_MODE
            ("PigZombieAlwaysAngry.Mode", MsgCategory.TUTORIAL),
    PIGZOMBIE_TARGET
            ("PigZombieAlwaysAngry.Msg", MsgCategory.TUTORIAL, "pigzombie",
                    "RUN! Pig Zombies are always angry and hungry!"),
    PIGZOMBIE_TARGET_WART_MODE
            ("PigZombieDropNetherwart.Mode", MsgCategory.TUTORIAL),
    PIGZOMBIE_TARGET_WART
            ("PigZombieDropNetherwart.Msg", MsgCategory.TUTORIAL, "pigzombie_wart",
                    "You can get netherwart from slaying Pig Zombies"),

    //Various Own Events
    ZOMBIE_RESPAWN_MODE
            ("ZombieRespawn.Mode", MsgCategory.TUTORIAL),
    ZOMBIE_RESPAWN
            ("ZombieRespawn.Msg", MsgCategory.TUTORIAL, "zombie_respawn",
                    "Zombies might resurrect if not on fire!"),
    ZOMBIE_SLOW_PLAYERS_MODE
            ("ZombieSlowPlayer.Mode", MsgCategory.TUTORIAL),
    ZOMBIE_SLOW_PLAYERS
            ("ZombieSlowPlayer.Msg", MsgCategory.TUTORIAL, "zombie_slow",
                    "Zombies slow you down when hit!"),
    SKELETON_DEFLECT_MODE
            ("SkeletonDeflectArrows.Mode", MsgCategory.TUTORIAL),
    SKELETON_DEFLECT
            ("SkeletonDeflectArrows.Msg", MsgCategory.TUTORIAL, "skeleton_deflect_arrow",
                    "Arrows just pass through Skeletons, you gotta go close combat!"),
    ENDERMAN_GENERAL_MODE
            ("EndermanGeneral.Mode", MsgCategory.TUTORIAL),
    ENDERMAN_GENERAL
            ("EndermanGeneral.Msg", MsgCategory.TUTORIAL, "enderman_teleport",
                    "Enderman can teleport you too!"),
    CREEPER_DROP_TNT_MODE
            ("CreeperDropTnt.Mode", MsgCategory.TUTORIAL),
    CREEPER_DROP_TNT
            ("CreeperDropTnt.Msg", MsgCategory.TUTORIAL, "creeper_drop_tnt",
                    "Creepers may drop activated tnt on death!"),
    EXTINGUISH_FIRE_MODE
            ("ExtinguishFire.Mode", MsgCategory.TUTORIAL),
    EXTINGUISH_FIRE
            ("ExtinguishFire.Msg", MsgCategory.TUTORIAL, "extinguish_fire",
                    "Putting out fire with your hand will catch you on fire."),
    LOST_ITEMS_MODE
            ("ListLostItemsOnDeath.Mode", MsgCategory.NOTIFICATION),
    LOST_ITEMS
            ("ListLostItemsOnDeath.Msg", MsgCategory.NOTIFICATION, "lost_items_broadcast",
                    "You managed to lose " + Variables.ITEMS.getVarName()),
    LOST_ITEMS_PLAYER_MODE
            ("InformPlayerOnLostItems.Mode", MsgCategory.TUTORIAL),
    LOST_ITEMS_PLAYER
            ("InformPlayerOnLostItems.Msg", MsgCategory.TUTORIAL, "lost_items",
                    "On death there is a a chance you might lose some of your items!"),

    //Farming
    BUCKET_FILL_MODE
            ("BucketFill.Mode", MsgCategory.TUTORIAL),
    BUCKET_FILL
            ("BucketFill.Msg", MsgCategory.TUTORIAL, "bucket_fill",
                    "You can pick up water, but once you place it, it evaporates. Get some ice if you want to farm!"),
    ANTIFARMING_UNWATERD_MODE
            ("AntifarmingUnwatered.Mode", MsgCategory.TUTORIAL),
    ANTIFARMING_UNWATERD
            ("AntifarmingUnwatered.Msg", MsgCategory.TUTORIAL, "antifarm_unwatered",
                    "Your crops need sufficient water, otherwise they\'ll dry out!"),
    ANTIFARMING_NO_LIGHT_MODE
            ("AntifarmingNotEnoughLight.Mode", MsgCategory.TUTORIAL),
    ANTIFARMING_NO_LIGHT
            ("AntifarmingNotEnoughLight.Msg", MsgCategory.TUTORIAL, "antifarm_natural_light",
                    "Your crops require natural light to grow!"),
    ANTIFARMING_DESSERT_WARNING_MODE
            ("AntifarmingDesert.Mode", MsgCategory.TUTORIAL),
    ANTIFARMING_DESSERT_WARNING
            ("AntifarmingDesert.Msg", MsgCategory.TUTORIAL, "antifarm_desert",
                    "Deserts are really dry and nothing grows here!"),
    ANIMAL_OVERCROWD_CONTROL_MODE
            ("AnimalOverCrowd.Mode", MsgCategory.NOTIFICATION),
    ANIMAL_OVERCROWD_CONTROL
            ("AnimalOverCrowd.Msg", MsgCategory.NOTIFICATION, "animal_overcrowd",
                "Animals need space! Consider putting them in a bigger area"),
    KELP_OR_SEAGRASS_CANT_CHANGE_FLOWING_WATER_MODE
            ("KelpOrSeagrassCantChangeFlowingWater.Mode", MsgCategory.NOTIFICATION),
    KELP_OR_SEAGRASS_CANT_CHANGE_FLOWING_WATER
            ("KelpOrSeagrassCantChangeFlowingWater.Msg", MsgCategory.NOTIFICATION, "kelp_or_seagrass_cant_change_flowing_water",
                "Kelp or seagrass are weak, so they cannot grow in flowing water."),
    
    //General Advice
    NETHER_WARNING_MODE
            ("NetherWarning.Mode", MsgCategory.TUTORIAL),
    NETHER_WARNING
            ("NetherWarning.Msg", MsgCategory.TUTORIAL, "nether_warn",
                    "This is a dangerous place. Make sure you come prepared with arrows and good gear."),
    EHM_CHAT_PREFIX("ChatPrefix", VarType.STRING, "[EHM]");

    /**
     * Path in the Config
     */
    private final String path;

    /**
     * Default value
     */
    private final Object value;

    /**
     * Messages are always strings.
     */
    private final VarType varType;

    /**
     * SubType for validation
     */
    private final SubType subType;

    /**
     * Name of the column to be used for persistence
     */
    private final String column;

    /**
     * How often this msg should be displayed to a Player (0 = off, -1 = infinite)
     */
    private final MsgCategory msgCategory;


    /**
     * Constructor for Mode Nodes
     *
     * @param path        path in yml
     * @param msgCategory mode of the message this nod eis for
     */
    private MessageNode(String path, MsgCategory msgCategory)
    {
        this.path = path;
        this.msgCategory = msgCategory;
        this.column = null;
        this.varType = VarType.STRING;
        this.subType = null;
        this.value = msgCategory.name().toLowerCase();
    }


    /**
     * Constructor.
     *
     * @param value - Default Value
     */
    private MessageNode(String path, MsgCategory msgCategory, String column, String value)
    {
        this.path = path;
        this.msgCategory = null; //This is important: Shows that this node actually holds a value and not the mode of a node
        this.column = column;
        this.varType = VarType.STRING;
        this.subType = null;
        this.value = value;
    }


    private MessageNode(String path, VarType varType, Object value)
    {
        this(path, varType, null, value);
    }


    private MessageNode(String path, VarType varType, SubType subType, Object value)
    {
        this.path = path;
        this.msgCategory = null;
        this.column = null;
        this.varType = varType;
        this.subType = subType;
        this.value = value;
    }


    @Override
    public String getPath()
    {
        return "Messages." + path;
    }


    @Override
    public VarType getVarType()
    {
        return varType;
    }


    @Override
    public SubType getSubType()
    {
        return subType;
    }


    @Override
    public Object getValueToDisable()
    {   /*ignored*/
        return null;
    }


    @Override
    public Object getDefaultValue()
    {
        return value;
    }


    /**
     * Get the name of the column. Used by our sqllite db for persistence.
     *
     * @return column name. Won't be changed.
     */
    public String getColumnName()
    {
        return column;
    }


    /**
     * Is this node only for the category
     */
    public boolean isCategoryNode()
    {
        return msgCategory != null;
    }


    /**
     * Get all nodes that hold a string value, aka a message to be displayed
     */
    public static Collection<MessageNode> getMessageNodes()
    {
        List<MessageNode> categories = new ArrayList<MessageNode>();
        for (MessageNode node : MessageNode.values())
            if (node.name().toUpperCase().endsWith("_MODE"))
            {
                MessageNode msg = null;
                try
                {
                    msg = MessageNode.valueOf(node.name().replace("_MODE", "")); //Every message has a node with the message and an accompanying node holding the type of message
                } catch (IllegalArgumentException ignored)
                {
                } finally
                {
                    if (msg != null)
                        categories.add(msg);
                }
            }
        return categories;
    }


    /**
     * Get all Nodes that hold the category of a node //TODO describe better
     */
    public static Collection<MessageNode> getCategoryNodes()
    {
        List<MessageNode> categories = new ArrayList<MessageNode>();
        for (MessageNode node : MessageNode.values())
            if (node.name().toUpperCase().endsWith("_MODE"))
                categories.add(node);
        return categories;
    }


    /**
     * Get the default category of this message.
     *
     * @return type
     *
     * @see MessageConfig#getCat(MessageNode)
     */
    public MsgCategory getDefaultCategory()
    {
        return msgCategory;
    }


    /**
     * Variables that will be filled in by the plugin
     */
    public enum Variables
    {
        //Search for this strings in the given order
        //Remove the support for $VAR and use §VAR instead
        //There were issues with $ being some kind of special char in yaml
        PLAYER("$PLAYER", "PLAYER"),
        PLAYERS("$PLAYERS", "PLAYERS"),
        ITEMS("$ITEMS", "ITEMS");

        private final String[] variable;


        private Variables(String... variable)
        {
            this.variable = variable;
        }


        /**
         * Get the identifiers which represents this variable in the String
         */
        public String[] getVarNames()
        {
            return variable.clone();
        }


        /**
         * Get the primary identifier which represents this variable in the String
         */
        public String getVarName()
        {
            return variable[0];
        }
    }
}
