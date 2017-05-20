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


import com.extrahardmode.service.config.ConfigNode;
import com.extrahardmode.service.config.MultiWorldConfig;
import com.extrahardmode.service.config.customtypes.BlockRelationsList;
import com.extrahardmode.service.config.customtypes.BlockType;
import com.extrahardmode.service.config.customtypes.BlockTypeList;
import com.extrahardmode.service.config.customtypes.PotionEffectHolder;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Configuration options of the root config.yml file.
 */
//Please keep the codestyle, it makes it easier to grasp the structure of the config
public enum RootNode implements ConfigNode
{
    /**
     * How this ConfigFile is going to be handled by the plugin
     */
    MODE("Config Type", VarType.STRING, "MAIN"),
    /**
     * Print Header?
     */
    PRINT_HEADER("Print Config Header", VarType.BOOLEAN, true, "If the big text on top of the config should be printed"),
    /**
     * Print Node Comments?
     */
    PRINT_COMMENTS("Print Comments", VarType.BOOLEAN, true, "If comments like this should be printed"),
    /**
     * list of worlds where extra hard mode rules apply
     */
    WORLDS("Enabled Worlds", VarType.LIST, new DefaultWorlds(),
            "Set the worlds you want ehm active here. F.e. [world, world_nether]. \"@all\" enables ehm for all worlds"),

    /**
     * #############
     * # BYPASSING #
     * #############
     */
    /**
     * If we should check for the bypass permission
     */
    BYPASS_PERMISSION("Bypassing.Check For Permission", VarType.BOOLEAN, true,
            "Disabling this disables all checks for bypass permissions."),
    /**
     * If players in creative auto bypass (useful for building)
     */
    BYPASS_CREATIVE("Bypassing.Creative Mode Bypasses", VarType.BOOLEAN, true,
            "Disable ehm for creative mode players. Useful for building."),
    /**
     * If players with op should bypass by default
     */
    BYPASS_OPS("Bypassing.Operators Bypass", VarType.BOOLEAN, false,
            "If activated ops automatically bypass. Disable if you want your ops to be able to play with ehm."),

    /**
     * ##################
     * # HARDENED STONE #
     * ##################
     */
    _COMMENT_HARD_STONE("World Rules.Mining.Inhibit Tunneling",
            "Hardened blocks require certain tools to be broken and will wear those tools down quicker",
            "This is to encourage caving, by making branch mining unprofitable"),
    /**
     * whether stone is hardened to encourage cave exploration over tunneling
     */
    SUPER_HARD_STONE("World Rules.Mining.Inhibit Tunneling.Enable", VarType.BOOLEAN, true,
            "If hardened blocks can only be broken by specific tools"),
    SUPER_HARD_BLOCKS("World Rules.Mining.Inhibit Tunneling.Hardened Blocks", VarType.BLOCKTYPE_LIST, new DefaultHardBlocks(),
            "These blocks will be treated as hardened"),
    /**
     * If ore placement next to stone blocks should be blocked to prevent tunneling
     */
    SUPER_HARD_STONE_BLOCK_ORE_PLACEMENT("World Rules.Mining.Inhibit Tunneling.Block Placing Ore Next To Stone Exploit", VarType.BOOLEAN, true,
            "Block players from placing ore next to stone to soften the stone when mining the ore."),
    /**
     * If movement of stone blocks with pistons should be blocked
     */
    SUPER_HARD_STONE_BLOCK_PISTONS("World Rules.Mining.Inhibit Tunneling.Block Moving Of Stone Blocks With Piston Exploit", VarType.BOOLEAN, true,
            "Block sneaky players from trying to tunnel using pistons. This will block movement of stone and ore blocks with pistons."),
    /**
     * whether stone is hardened to encourage cave exploration over tunneling
     */
    SUPER_HARD_STONE_TOOLS("World Rules.Mining.Inhibit Tunneling.Amount of Stone Tool Can Mine (Tool@Blocks)", VarType.BLOCKTYPE_LIST, new DefaultToolDurabilities(),
            "List of tools that can mine stone. If a tool isn't in the list it can't mine stone.",
            "F.e. DIAMOND_PICKAXE@100 = Mine 100 stone blocks -> pick broken"),
    /**
     * Breaking an ore will cause surrounding stone to turn to cobble and fall
     */
    SUPER_HARD_STONE_PHYSICS("World Rules.Mining.Breaking Blocks Softens Surrounding Stone.Enable", VarType.BOOLEAN, true,
            "Cave-ins are a persistent threat. Mining ore softens the stone around it, which can then fall and injure the careless player.",
            "Dirt and grass, which is often compacted into a solid mass in cavern ceilings and floors, will also come crashing down when disturbed."),
    /**
     * Apply physics to blocks surrounding stone
     */
    SUPER_HARD_STONE_PHYSICS_APPLY("World Rules.Mining.Breaking Blocks Softens Surrounding Stone.Apply Physics To Weakened Stone", VarType.BOOLEAN, true,
            "If the softened stone blocks should fall. They do not have to be in additional falling blocks section for this."),
    /**
     * These Blocks will turn surrounding stone into cobblestone
     */
    SUPER_HARD_STONE_ORE_BLOCKS("World Rules.Mining.Breaking Blocks Softens Surrounding Stone.Blocks (Block@id,id2)", VarType.BLOCKTYPE_LIST, new DefaultPhysicsBlocks(),
            "Ore blocks that will soften surrounding stone blocks."),
    /**
     * Stone Blocks and their counter respective cobblestone blocks
     */
    SUPER_HARD_STONE_STONE_BLOCKS("World Rules.Mining.Breaking Blocks Softens Surrounding Stone.Stone Blocks (Stone@data-Cobble@data)", VarType.BLOCK_RELATION_LIST, new DefaultStoneBlocks(),
            "Here you can specify custom stone blocks or change what stone softens into."),
    /**
     * ###########
     * # TORCHES #
     * ###########
     */
    /**
     * maximum y for placing standard torches
     */
    STANDARD_TORCH_MIN_Y("World Rules.Torches.No Placement Under Y", VarType.INTEGER, SubType.Y_VALUE, Disable.ZERO, 30,
            "No placement of torches below Defined Value. Makes for scarier caves on the lower levels. Y: 0 to disable"),
    /**
     * whether players are limited to placing torches against specific materials
     */
    LIMITED_TORCH_PLACEMENT("World Rules.Torches.No Placement On Soft Materials", VarType.BOOLEAN, true,
            "Soft materials include sand and dirt. Idea is that players don't litter the landscape with torches."),
    /**
     * whether rain should break torches
     */
    RAIN_BREAKS_TORCHES("World Rules.Torches.Rain Breaks Torches", VarType.BOOLEAN, true,
            "When it rains there is a chance that torches will be removed in a chunk.",
            "Any kind of block above the torch is enough to protect the torch"),

    /**
     * ##########
     * # SOUNDS #
     * ##########
     */
    /**
     * Sound when torch placing fails
     */
    SOUNDS_TORCH_FIZZ("World Rules.Play Sounds.Torch Fizzing", VarType.BOOLEAN, true,
            "A lava fizz when a torch's placement has been blocked."),
    /**
     * Warning Sound when a creeper drops tnt
     */
    SOUND_CREEPER_TNT("World Rules.Play Sounds.Creeper Tnt Warning", VarType.BOOLEAN, true,
            "A Ghast shriek when a creeper drops tnt."),
    /**
     * #################
     * # WORLD EFFECTS #
     * #################
     */
    /**
     * percent chance for broken netherrack to start a fire
     */
    BROKEN_NETHERRACK_CATCHES_FIRE_PERCENT("World Rules.Breaking Netherrack Starts Fire Percent", VarType.INTEGER, SubType.PERCENTAGE, 20,
            "Tunneling in the nether will randomly set a fire. Players have to be careful",
            "to not set themselves on fire."),
    /**
     * whether players may place blocks directly underneath themselves
     */
    LIMITED_BLOCK_PLACEMENT("World Rules.Limited Block Placement", VarType.BOOLEAN, true,
            "Blocks jumping and placing a block directly beneath you and branching out with no blocks to support"),
    /**
     * whether tree logs respect gravity
     */
    BETTER_TREE_CHOPPING("World Rules.Better Tree Felling", VarType.BOOLEAN, true,
            "The trunk and branches of a tree will fall and potentially injure you.",
            "It makes it easier to chop trees, but you have to watch out a little for the falling logs.",
            "Also by making logs of branches fall down most treetops should decay naturally."),
    /**
     * #################################
     * # ENHANCED ENVIRONMENTAL DAMAGE #
     * #################################
     */
    /**
     * whether players take additional damage and/or debuffs from environmental injuries
     */
    //dmg before potion effect -> first value in list
    _COMMENT_ENVIRONMENTAL_DMG("Player.Enhanced Environmental Injuries",
            "Apply effects and damage multipliers to players",
            "Bukkit effect names: http://jd.bukkit.org/rb/apidocs/org/bukkit/potion/PotionEffectType.html",
            "or effect ids: http://minecraft.gamepedia.com/Status_effects"),
    ENHANCED_ENVIRONMENTAL_DAMAGE("Player.Enhanced Environmental Injuries.Enable", VarType.BOOLEAN, true),

    ENHANCED_DMG_FALL_MULT("Player.Enhanced Environmental Injuries.Fall.Dmg Multiplier", VarType.DOUBLE, 2.0),

    ENHANCED_DMG_FALL("Player.Enhanced Environmental Injuries.Fall", VarType.POTION_EFFECT, new PotionEffectHolder(PotionEffectType.SLOW, 4 * 20, 2)),

    ENHANCED_DMG_EXPLOSION_MULT("Player.Enhanced Environmental Injuries.Explosion.Dmg Multiplier", VarType.DOUBLE, 1.0),

    ENHANCED_DMG_EXPLOSION("Player.Enhanced Environmental Injuries.Explosion", VarType.POTION_EFFECT, new PotionEffectHolder(PotionEffectType.CONFUSION, 15 * 20, 3)),

    ENHANCED_DMG_SUFFOCATION_MULT("Player.Enhanced Environmental Injuries.Suffocation.Dmg Multiplier", VarType.DOUBLE, 5.0),

    ENHANCED_DMG_SUFFOCATION("Player.Enhanced Environmental Injuries.Suffocation", VarType.POTION_EFFECT, new PotionEffectHolder(null, 0, 0)),

    ENHANCED_DMG_LAVA_MULT("Player.Enhanced Environmental Injuries.Lava.Dmg Multiplier", VarType.DOUBLE, 2.0),

    ENHANCED_DMG_LAVA("Player.Enhanced Environmental Injuries.Lava", VarType.POTION_EFFECT, new PotionEffectHolder(null, 0, 0)),

    ENHANCED_DMG_BURN_MULT("Player.Enhanced Environmental Injuries.Burning.Dmg Multiplier", VarType.DOUBLE, 1.0),

    ENHANCED_DMG_BURN("Player.Enhanced Environmental Injuries.Burning", VarType.POTION_EFFECT, new PotionEffectHolder(PotionEffectType.BLINDNESS, 20, 1)),

    ENHANCED_DMG_STARVATION_MULT("Player.Enhanced Environmental Injuries.Starvation.Dmg Multiplier", VarType.DOUBLE, 2.0),

    ENHANCED_DMG_STARVATION("Player.Enhanced Environmental Injuries.Starvation", VarType.POTION_EFFECT, new PotionEffectHolder(null, 0, 0)),

    ENHANCED_DMG_DROWNING_MULT("Player.Enhanced Environmental Injuries.Drowning.Dmg Multiplier", VarType.DOUBLE, 2.0),

    ENHANCED_DMG_DROWNING("Player.Enhanced Environmental Injuries.Drowning", VarType.POTION_EFFECT, new PotionEffectHolder(null, 0, 0)),

    /**
     * whether players catch fire when extinguishing a fire up close
     */
    EXTINGUISHING_FIRE_IGNITES_PLAYERS("Player.Extinguishing Fires Ignites Player", VarType.BOOLEAN, true,
            "Set the player on fire when he tries to extinguish fire with his bare hand."),
    /**
     * ################
     * # PLAYER DEATH #
     * ################
     */
    _COMMENT_PLAYER_DEATH("Player.Death",
            "On death, a small portion of the player's inventory disappears forever,",
            "discouraging players from killing themselves to restore health and hunger.",
            "After respawn, the player won't have a full health and food bar."),
    /**
     * Enabled item loss on death
     */
    PLAYER_DEATH_ITEMS_FORFEIT_ENABLE("Player.Death.Loose Items On Death.Enable", VarType.BOOLEAN, true),
    /**
     * percentage of item stacks lost on death
     */
    PLAYER_DEATH_ITEM_STACKS_FORFEIT_PERCENT("Player.Death.Loose Items On Death.Percentage", VarType.INTEGER, SubType.PERCENTAGE, 10,
            "Percentage of all of the players items that will get lost on death."),
    /**
     * Damage Tools instead by a percentage of their max durability instead of completely deleting the items
     */
    PLAYER_DEATH_TOOLS_DMG_PERCENTAGE("Player.Death.Loose Items On Death.Damage Tools By Percentage", VarType.INTEGER, SubType.PERCENTAGE, 30,
            "Damage some tools from the list instead of completely removing them.",
            "Encourages players to use more valuable tools as they won't completely loose them on death."),
    /**
     * If a tool would be completely destroyed if we should keep it
     */
    PLAYER_DEATH_TOOLS_KEEP_DAMAGED("Player.Death.Loose Items On Death.Keep Heavily Damaged Tools", VarType.BOOLEAN, true,
            "If an already heavily damaged tool should be kept or completely destroyed."),
    /**
     * List of items that count as tools
     */
    PLAYER_DEATH_TOOLS_LIST("Player.Death.Loose Items On Death.Tools", VarType.BLOCKTYPE_LIST, new DefaultValuableTools(),
            "Tool settings apply only to these tools"),
    PLAYER_DEATH_ITEMS_BLACKLIST("Player.Death.Loose Items On Death.Blacklisted Items", VarType.BLOCKTYPE_LIST, BlockTypeList.EMPTY_LIST,
            "These items will never be removed on death."),
    /**
     * Enable custom Health
     */
    PLAYER_RESPAWN_HEALTH_ENABLE("Player.Death.Override Respawn Health.Enable", VarType.BOOLEAN, true),
    /**
     * how much health after respawn
     */
    PLAYER_RESPAWN_HEALTH_PERCENTAGE("Player.Death.Override Respawn Health.Percentage", VarType.INTEGER, SubType.PERCENTAGE, Disable.HUNDRED, 75,
            "Percentage of total health that the player will spawn with. Works with custom max health."),
    /**
     * how much food bar after respawn
     */
    PLAYER_RESPAWN_FOOD_LEVEL("Player.Death.Respawn Foodlevel", VarType.INTEGER, SubType.HEALTH, 15,
            "How many food hunches a player will spawn with"),
    /**
     * #########################
     * # SWIMMING RESTRICTIONS #
     * #########################
     */
    _COMMENT_SWIMMING("Player.No Swimming When Too Heavy",
            "Adds a weight system to your inventory. If your inventory exceeds the weight you will be pulled down ",
            "and eventually drown. This is to encourage players to use boats and make swimming up waterfalls harder."),
    /**
     * whether players may swim while wearing armor
     */
    NO_SWIMMING_IN_ARMOR("Player.No Swimming When Too Heavy.Enable", VarType.BOOLEAN, true),
    /**
     * Block Swimming Up WaterFalls/WaterElevators
     */
    NO_SWIMMING_IN_ARMOR_BLOCK_ELEVATORS("Player.No Swimming When Too Heavy.Block Elevators/Waterfalls", VarType.BOOLEAN, true,
            "Set to false if you want to exempt players from drowning when swimming up 1x1 water streams."),
    /**
     * The maximum amount of points you can have before being too heavy
     */
    NO_SWIMMING_IN_ARMOR_MAX_POINTS("Player.No Swimming When Too Heavy.Max Points", VarType.DOUBLE, 18.0,
            "The maximum inventory weight you can have before starting to drown."),
    /**
     * The amount of points a piece of armor adds to the max
     */
    NO_SWIMMING_IN_ARMOR_ARMOR_POINTS("Player.No Swimming When Too Heavy.One Piece Of Worn Armor Adds", VarType.DOUBLE, 2.0,
            "One piece of worn armor would add 2.0 weight. So full set of armor adds 8.0"),
    /**
     * The amount of points that stuff in your inventory adds to the max
     */
    NO_SWIMMING_IN_ARMOR_INV_POINTS("Player.No Swimming When Too Heavy.One Stack Adds", VarType.DOUBLE, 1.0,
            "A stack of any item adds 1.0, half a stack add 0.5 so it calculates fractions"),
    /**
     * How much a tool or item which doesn't stack adds to the max
     */
    NO_SWIMMING_IN_ARMOR_TOOL_POINTS("Player.No Swimming When Too Heavy.One Tool Adds", VarType.DOUBLE, 0.5,
            "A tool is any item that doesn't stack, swords, axes, not worn armor, shears etc"),
    /**
     * How fast do you drown, 100 (percent) = you drown no chance, 25 there is a chance you'll drown
     */
    NO_SWIMMING_IN_ARMOR_DROWN_RATE("Player.No Swimming When Too Heavy.Drown Rate", VarType.INTEGER, SubType.NATURAL_NUMBER, 35,
            "Basically an esoteric percentage of how fast you drown. 35 actually doesnt really make you drown. 50 would make you drown"),
    /**
     * How much do you drown faster per weight over the max
     */
    NO_SWIMMING_IN_ARMOR_ENCUMBRANCE_EXTRA("Player.No Swimming When Too Heavy.Overencumbrance Adds To Drown Rate", VarType.INTEGER, SubType.NATURAL_NUMBER, 2,
            "If your inventory weight exceeds the max weight every weightpoint will add 2 to the drownrate.",
            "Weight = 25 => (base) + (exceeding) * (modifier) = 35 + 7 * 2 = 49 (new drown rate)"),

    /**
     * #################
     * # ARMOR CHANGES #
     * #################
     */

    /**
     * If wearing armor should slow a player down
     */
    ARMOR_SLOWDOWN_ENABLE("Player.Armor Changes.Enable", VarType.BOOLEAN, true,
            "Enables slowdown of players wearing armor."),
    /**
     * Speed of player walking with no worn armor
     */
    ARMOR_SLOWDOWN_BASESPEED("Player.Armor Changes.Basespeed", VarType.DOUBLE, 0.22,
            "Player speed with no armor. Minecraft default is 0.2.",
            "Slightly increased to 0.22 to give players with no armor an advantage."),
    /**
     * Maximum percentage
     */
    ARMOR_SLOWDOWN_PERCENT("Player.Armor Changes.Slowdown Percentage", VarType.INTEGER, SubType.PERCENTAGE, 40,
            "How much percent players wearing a full diamond armor will be slowed down.",
            "This is the maximum slow down, the amount of armor points determines how much a player will be slowed down."),
//    /**
//     * Maximum percentage
//     */
//    ARMOR_JUMP_SLOWDOWN_PERCENT("Player.Armor Changes.Jump Slowdown Percentage", VarType.INTEGER, SubType.PERCENTAGE, 30,
//            "By how much players jumping (esp. sprint jumping) will be slowed down."),

    /**
     * #########################
     * # GENERAL MONSTER RULES #
     * #########################
     */
    /**
     * whether monster grinders (or "farms") should be inhibited
     */
    INHIBIT_MONSTER_GRINDERS("General Monster Rules.Inhibit Monster Grinders", VarType.BOOLEAN, true,
            "This is an advanced anti monster grinder module. It will block drops if the monster",
            "spawned on an unnatural block, took too much damage from natural causes (falldmg etc.)",
            "cant reach a player or can not easily reach a player f.e. monster is in water."),
    /**
     * max y value for extra monster spawns
     */
    MORE_MONSTERS_MAX_Y("General Monster Rules.More Monsters.Max Y", VarType.INTEGER, SubType.Y_VALUE, Disable.ZERO, 55),
    /**
     * what to multiply monster spawns by
     */
    MORE_MONSTERS_MULTIPLIER("General Monster Rules.More Monsters.Multiplier", VarType.INTEGER, SubType.NATURAL_NUMBER, Disable.ONE, 2,
            "A simple multiplier to increase spawns under ground by increasing the packspawning size."),

    /**
     * max y value for monsters to spawn in the light
     */
    MONSTER_SPAWNS_IN_LIGHT_MAX_Y("General Monster Rules.Monsters Spawn In Light.Max Y", VarType.INTEGER, SubType.Y_VALUE, Disable.ZERO, 50),

    /**
     * max light value for monsters to spawn in the light
     * 0-3  = bats spawn at depth
     * 0-7  = mobs spawn in overworld normally
     * 8-11 = mobs are hostile but do not burn
     * 12+  = mobs except spiders, creepers, and witches burn (blazes hostile)
     */
    MONSTER_SPAWNS_IN_LIGHT_MAX_LIGHT("General Monster Rules.Monsters Spawn In Light.Max Light", VarType.INTEGER, SubType.NATURAL_NUMBER, Disable.ZERO, 10,
            "0-3: bats spawning, 0-7 normal mob spawning, 8-11 mobs are hostile but don't burn, 12+ mobs burn"),

    /**
     * percentage of time to spawn monsters in light
     */
    MONSTER_SPAWNS_IN_LIGHT_PERCENTAGE("General Monster Rules.Monsters Spawn In Light.Percentage", VarType.INTEGER, SubType.PERCENTAGE, Disable.ZERO, 100,
            "Spawns monsters at locations where player has been previously."),

    /**
     * ##########
     * # HORSES #
     * ##########
     */
    HORSE_CHEST_BLOCK_BELOW("Horses.Block Usage Of Chest Below", VarType.INTEGER, SubType.Y_VALUE, Disable.ZERO, 55),

    /**
     * ###########
     * # ZOMBIES #
     * ###########
     */
    _COMMENT_ZOMBIES("Zombies", "Instead of speeding Zombies up, a Zombie will slow a player down for a few seconds when the player is hit by a zombie.",
            "Zombies may resurrect when slain. They will respawn after a few seconds and might ambush a player."),
    /**
     * whether zombies apply a debuff to players on hit
     */
    ZOMBIES_DEBILITATE_PLAYERS("Zombies.Slow Players.Enable", VarType.BOOLEAN, true),

    ZOMBIES_DEBILITATE_PLAYERS_EFFECT("Zombies.Slow Players.Effect", VarType.POTION_EFFECT, new PotionEffectHolder(PotionEffectType.SLOW, 5 * 20, 1),
            "Effect to apply to the player when he is hit."),

    ZOMBIES_DEBILITATE_PLAYERS_EFFECT_STACK("Zombies.Slow Players.Stack Effect.Enable", VarType.BOOLEAN, true,
            "If the effect strength should be increased when a player is hit in succession"),

    ZOMBIES_DEBILITATE_PLAYERS_EFFECT_STACK_MAX("Zombies.Slow Players.Stack Effect.Max Strength", VarType.INTEGER, SubType.NATURAL_NUMBER, 3,
            "Maximum strength of the effect that can be achieved."),

    /**
     * percent chance for a zombie to reanimate after death
     */
    ZOMBIES_REANIMATE_SKULLS("Zombies.Reanimate.Place Skulls", VarType.BOOLEAN, true,
            "If zombie heads should be placed at the location where a zombie will resurrect",
            "Breaking the head will result in the zombie not resurrecting."),
    ZOMBIE_REANIMATE_SKULLS_DROP_PERCENTAGE("Zombies.Reanimate.Placed Skulls Drop Percentage", VarType.INTEGER, SubType.PERCENTAGE, 5,
            "What percentage of the placed skulls should drop as an item, when broken before the zombie respawns."),
    ZOMBIES_REANIMATE_PERCENT("Zombies.Reanimate.Percent", VarType.INTEGER, SubType.PERCENTAGE, 50,
            "Percentage for the 1st respawn to occur. To reduce the amount of consecutive respawns the percentage reduced by 1/n respawns.",
            "F.e 1: 50%, 2: 1/2 of 50% = 25%, 3: 1/3 of 25% = 7.5% and so on"),

    /**
     * #############
     * # SKELETONS #
     * #############
     */
    /**
     * Enable Snowball Arrows
     */
    SKELETONS_SNOWBALLS_ENABLE("Skeletons.Shoot Snowballs.Enable", VarType.BOOLEAN, true),
    /**
     * How often should a snowball be shot
     */
    SKELETONS_SNOWBALLS_PERCENT("Skeletons.Shoot Snowballs.Percent", VarType.INTEGER, SubType.PERCENTAGE, 20),
    /**
     * Slowness length
     */
    SKELETONS_SNOWBALLS_SLOW_LEN("Skeletons.Shoot Snowballs.Blind Player (ticks)", VarType.INTEGER, SubType.NATURAL_NUMBER, 100),

    /**
     * Shoot Fireworks
     */
    SKELETONS_FIREWORK_ENABLE("Skeletons.Shoot Fireworks.Enable", VarType.BOOLEAN, true),
    /**
     * Knockback Players?
     */
    SKELETONS_FIREWORK_PERCENT("Skeletons.Shoot Fireworks.Percent", VarType.INTEGER, SubType.PERCENTAGE, 30),
    /**
     * Knockback Player strength, multiplier
     */
    SKELETONS_FIREWORK_KNOCKBACK_VEL("Skeletons.Shoot Fireworks.Knockback Player Velocity", VarType.DOUBLE, 1.0D),

    /**
     * Skeletons can shoot fireballs whcih set you on fire
     */
    SKELETONS_FIREBALL_ENABLE("Skeletons.Shoot Fireballs.Enable", VarType.BOOLEAN, true),


    SKELETONS_FIREBALL_PERCENTAGE("Skeletons.Shoot Fireballs.Percentage", VarType.INTEGER, SubType.PERCENTAGE, 10),


    SKELETONS_FIREBALL_PLAYER_FIRETICKS("Skeletons.Shoot Fireballs.Player Fireticks", VarType.INTEGER, SubType.NATURAL_NUMBER, 40),

    /**
     * enable skeletons shooting silverfish instead of firing arrows
     */
    SKELETONS_RELEASE_SILVERFISH_ENABLE("Skeletons.Shoot Silverfish.Enable", VarType.BOOLEAN, true),
    /**
     * percent chance skeletons will release silverfish instead of firing arrows
     */
    SKELETONS_RELEASE_SILVERFISH_PERCENT("Skeletons.Shoot Silverfish.Percent", VarType.INTEGER, SubType.PERCENTAGE, 20),
    /**
     * Kill the silverfish after the skeleton died
     */
    SKELETONS_RELEASE_SILVERFISH_KILL("Skeletons.Shoot Silverfish.Kill Silverfish After Skeleton Died", VarType.BOOLEAN, true),
    /**
     * percent chance skeletons will release silverfish instead of firing arrows
     */
    SKELETONS_RELEASE_SILVERFISH_LIMIT("Skeletons.Shoot Silverfish.Limit To X Spawned At A Time", VarType.INTEGER, SubType.NATURAL_NUMBER, 5),
    /**
     * total limit of silverfish
     */
    SKELETONS_RELEASE_SILVERFISH_LIMIT_TOTAL("Skeletons.Shoot Silverfish.Limit To X Spawned In Total", VarType.INTEGER, SubType.NATURAL_NUMBER, 15),
    /**
     * whether or not arrows will pass harmlessly through skeletons
     */
    SKELETONS_DEFLECT_ARROWS("Skeletons.Deflect Arrows Percent", VarType.INTEGER, SubType.PERCENTAGE, 100),

    /**
     * ##############
     * # SILVERFISH #
     * ##############
     */
    /**
     * If Silverfish cant enter stone etc and turn it into a silverfish block
     */
    SILVERFISH_CANT_ENTER_BLOCKS("Silverfish.Cant enter blocks", VarType.BOOLEAN, true),
    /**
     * If spawned silverfish drop cobble on death
     */
    SILVERFISH_DROP_COBBLE("Silverfish.Drop Cobble", VarType.BOOLEAN, true),
    /**
     * Spawn with a potion effect so you can still see them when they glitch into the floor
     */
    SILVERFISH_TEMP_POTION_EFFECT_FIX("Silverfish.Show Particles To Make Better Visible", VarType.BOOLEAN, true),

    /**
     * ###########
     * # SPIDERS #
     * ###########
     */
    /**
     * percentage of zombies which will be replaced with spiders under sea level
     */
    BONUS_UNDERGROUND_SPIDER_SPAWN_PERCENT("Spiders.Bonus Underground Spawn Percent", VarType.INTEGER, SubType.PERCENTAGE, 20),
    /**
     * whether spiders drop webbing when they die
     */
    SPIDERS_DROP_WEB_ON_DEATH("Spiders.Drop Web On Death", VarType.BOOLEAN, true),

    /**
     * ############
     * # CREEPERS #
     * ############
     */
    /**
     * percentage of creepers which will spawn charged
     */
    CHARGED_CREEPER_SPAWN_PERCENT("Creepers.Charged Creeper Spawn Percent", VarType.INTEGER, SubType.PERCENTAGE, 10),
    /**
     * percentage of creepers which spawn activated TNT on death
     */
    CREEPERS_DROP_TNT_ON_DEATH_PERCENT("Creepers.Drop Tnt On Death.Percent", VarType.INTEGER, SubType.PERCENTAGE, 20),
    /**
     * max y for creepers to drop tnt, to restrict them to caves
     */
    CREEPERS_DROP_TNT_ON_DEATH_MAX_Y("Creepers.Drop Tnt On Death.Max Y", VarType.INTEGER, SubType.Y_VALUE, Disable.ZERO, 50),
    /**
     * whether charged creepers explode when damaged
     */
    CHARGED_CREEPERS_EXPLODE_ON_HIT("Creepers.Charged Creepers Explode On Damage", VarType.BOOLEAN, true),
    /**
     * whether creepers explode when caught on fire
     */
    FLAMING_CREEPERS_EXPLODE("Creepers.Fire Triggers Explosion.Enable", VarType.BOOLEAN, true),
    /**
     * Number of Fireworks to show when creeper launches
     */
    FLAMING_CREEPERS_FIREWORK("Creepers.Fire Triggers Explosion.Firework Count", VarType.INTEGER, SubType.NATURAL_NUMBER, 3),
    /**
     * Speed at which a creeper ascends
     */
    FLAMING_CREEPERS_ROCKET("Creepers.Fire Triggers Explosion.Launch In Air Speed", VarType.DOUBLE, 0.5),

    /**
     * ##########
     * # BLAZES #
     * ##########
     */
    /**
     * percentage of skeletons near bedrock which will be replaced with blazes
     */
    NEAR_BEDROCK_BLAZE_SPAWN_PERCENT("Blazes.Near Bedrock Spawn Percent", VarType.INTEGER, SubType.PERCENTAGE, 50),
    /**
     * Should drops be blocked in the overworld
     */
    BLAZES_BLOCK_DROPS_OVERWORLD("Blazes.Block Drops In Overworld", VarType.BOOLEAN, true),
    /**
     * percentage of pig zombies which will be replaced with blazes
     */
    BONUS_NETHER_BLAZE_SPAWN_PERCENT("Blazes.Bonus Nether Spawn Percent", VarType.INTEGER, SubType.PERCENTAGE, 20),
    /**
     * whether blazes drop fire when damaged
     */
    BLAZES_DROP_FIRE_ON_DAMAGE("Blazes.Drop Fire On Damage", VarType.BOOLEAN, true),
    /**
     * whether blazes drop extra loot
     */
    BLAZES_DROP_BONUS_LOOT("Blazes.Bonus Loot", VarType.BOOLEAN, true),
    /**
     * percentage chance that a blaze slain in the nether will split into two blazes
     */
    NETHER_BLAZES_SPLIT_ON_DEATH_PERCENT("Blazes.Nether Split On Death Percent", VarType.INTEGER, SubType.PERCENTAGE, 25),

    /**
     * percentage chance that a blaze spawn will trigger a flame slime spawn as well
     */
    FLAME_SLIMES_SPAWN_WITH_NETHER_BLAZE_PERCENT("MagmaCubes.Spawn With Nether Blaze Percent", VarType.INTEGER, SubType.PERCENTAGE, 100),
    /**
     * whether damaging a magma cube turns it into a blaze
     */
    MAGMA_CUBES_BECOME_BLAZES_ON_DAMAGE("MagmaCubes.Grow Into Blazes On Damage", VarType.BOOLEAN, true),

    /**
     * ##############
     * # PIGZOMBIES #
     * ##############
     */
    /**
     * whether pig zombies are always hostile
     */
    ALWAYS_ANGRY_PIG_ZOMBIES("PigZombies.Always Angry", VarType.BOOLEAN, true),
    /**
     * Reduce dmg from pigmen because it's not meant to be a hostile mob
     */
    PIG_ZOMBIE_DMG_PERCENT("PigZombies.Dmg to players percent", VarType.INTEGER, SubType.NATURAL_NUMBER, 70,
            "This simple multiplier allows you to reduce the damage of PigZombies. They are a bit too tough otherwise."),
    /**
     * whether pig zombies always drop nether wart in nether fortresses
     */
    FORTRESS_PIGS_DROP_WART("PigZombies.Always Drop Netherwart In Fortresses", VarType.BOOLEAN, true,
            "Add netherwart to the drops of pigzombies."),
    /**
     * Whether pig zombies should drop netherwart occasionally elsewhere in Nether
     */
    NETHER_PIGS_DROP_WART("PigZombies.Percent Chance to Drop Netherwart Elsewhere In Nether", VarType.INTEGER, SubType.PERCENTAGE, 25),
    /**
     * PigMen get spawned when lighting strikes
     */
    LIGHTNING_SPAWNS_PIGMEN("PigZombies.Spawn on Lighting Strikes.Enable", VarType.BOOLEAN, true),
    /**
     * ##########
     * # GHASTS #
     * ##########
     */
    /**
     * whether ghasts should deflect arrows and drop extra loot percentage like skeleton deflect
     */
    GHASTS_DEFLECT_ARROWS("Ghasts.Arrows Do % Damage", VarType.INTEGER, SubType.PERCENTAGE, Disable.HUNDRED, 20,
            "Reduce the damage arrows do to Ghasts to make fights with Ghasts more challenging."),
    /**
     * whether ghasts should deflect arrows and drop extra loot percentage like skeleton deflect
     */
    GHASTS_EXP_MULTIPLIER("Ghasts.Exp Multiplier", VarType.INTEGER, SubType.NATURAL_NUMBER, Disable.ONE, 10),
    /**
     * whether ghasts should deflect arrows and drop extra loot percentage like skeleton deflect
     */
    GHASTS_DROPS_MULTIPLIER("Ghasts.Drops Multiplier", VarType.INTEGER, SubType.NATURAL_NUMBER, Disable.ONE, 5),
    /**
     * ############
     * # ENDERMAN #
     * ############
     */
    /**
     * whether endermen may teleport players
     */
    IMPROVED_ENDERMAN_TELEPORTATION("Endermen.May Teleport Players", VarType.BOOLEAN, true,
            "No more easy killing by standing under a 3 high roof! An enderman may teleport a Player. Makes fights with enderman challenging and dangerous."),

    /**
     * ###########
     * # WITCHES #
     * ###########
     */
    /**
     * Do Witches have additional attacks
     */
    WITCHES_ADDITIONAL_ATTACKS("Witches.Additional Attacks", VarType.BOOLEAN, true,
            "Includes spawning of baby zombies, explosions and teleporting"),
    /**
     * percentage of surface zombies which spawn as witches
     */
    BONUS_WITCH_SPAWN_PERCENT("Witches.Bonus Spawn Percent", VarType.INTEGER, SubType.PERCENTAGE, 5),

    /**
     * ###########
     * # KillerBunny #
     * ###########
     */
    /**
     * percentage of rabbits which spawn as killerbunnies
     */
    BONUS_KILLERBUNNY_SPAWN_PERCENT("KillerBunny.Bonus Spawn Percent", VarType.INTEGER, SubType.PERCENTAGE, 1),
    
    /**
     * ###########
     * # Vindicator #
     * ###########
     */
    /**
     * percentage of skeletons which spawn as vindicators in roofed forests.
     */
    BONUS_VINDICATOR_SPAWN_PERCENT("Vindicator.Bonus Spawn Percent", VarType.INTEGER, SubType.PERCENTAGE, 20),
    /**
     * ###########
     * # Cave Spider #
     * ###########
     */
    /**
     * percentage of spiders which spawn as cave spiders in swamplands.
     */
    BONUS_CAVESPIDER_SPAWN_PERCENT("CaveSpider.Bonus Spawn Percent", VarType.INTEGER, SubType.PERCENTAGE, 5),
    /**
     * ###########
     * # Guardians #
     * ###########
     */
    /**
     * percentage of squids which spawn as Guardians in ocean and deep ocean.
     */
    BONUS_GUARDIANS_SPAWN_PERCENT("Guardians.Bonus Spawn Percent", VarType.INTEGER, SubType.PERCENTAGE, 20),
    /**
     * ###########
     * # VEX #
     * ###########
     */
    /**
     * percentage of bats which spawn as Vex.
     */
    BONUS_VEX_SPAWN_PERCENT("Vex.Bonus Spawn Percent", VarType.INTEGER, SubType.PERCENTAGE, 5),
    /**
     * ################
     * # ENDER DRAGON #
     * ################
     */
    /**
     * whether the ender dragon respawns
     */
    RESPAWN_ENDER_DRAGON("EnderDragon.Respawns", VarType.BOOLEAN, true),
    /**
     * whether it drops an egg when slain
     */
    ENDER_DRAGON_DROPS_EGG("EnderDragon.Drops Dragonegg", VarType.BOOLEAN, true),
    /**
     * whether it drops a pair of villager eggs when slain
     */
    ENDER_DRAGON_DROPS_VILLAGER_EGGS("EnderDragon.Drops 2 Villager Eggs", VarType.BOOLEAN, true),
    /**
     * whether the dragon spits fireballs and summons minions
     */
    ENDER_DRAGON_ADDITIONAL_ATTACKS("EnderDragon.Harder Battle", VarType.BOOLEAN, true,
            "Dragon summons minions including blazes and zombies. Can also aggro nearby endermen!"),
    /**
     * whether the dragon summons Erik1988's variety of mobs
     */
    ALTERNATIVE_FIREBALL("EnderDragon.Alternative Minions", VarType.BOOLEAN, false,
            "More diverse mobs are summoned (blazes, zombies, and skeletons). Harder Battle must be set to true, or this option has no effect."),
    /**
     * whether server wide messages will broadcast player victories and defeats
     */
    ENDER_DRAGON_COMBAT_ANNOUNCEMENTS("EnderDragon.Battle Announcements", VarType.BOOLEAN, true,
            "Announces in chat when someone is challenging the dragon or has beaten her."),
    /**
     * whether players will be allowed to build in the end
     */
    ENDER_DRAGON_NO_BUILDING("EnderDragon.No Building Allowed", VarType.BOOLEAN, true,
            "Block building in the end to prevent players from building big protective structures."),
    /**
     * Sets the health of the Ender Dragon (200 is vanilla)
     */
    ENDER_DRAGON_HEALTH("EnderDragon.Health", VarType.INTEGER, SubType.NATURAL_NUMBER, Disable.ZERO, 800,
            "Sets the health of the Ender Dragon"),
    /**
     * ###########
     * # FARMING #
     * ###########
     */
    /**
     * whether food crops die more easily
     */
    WEAK_FOOD_CROPS("Farming.Weak Crops.Enable", VarType.BOOLEAN, true),
    /**
     * How much percent of plants you loose
     */
    WEAK_FOOD_CROPS_LOSS_RATE("Farming.Weak Crops.Loss Rate", VarType.INTEGER, SubType.PERCENTAGE, Disable.ZERO, 25),
    /**
     * Should desserts be really empty and hostile towards plants
     */
    ARID_DESSERTS("Farming.Weak Crops.Infertile Deserts", VarType.BOOLEAN, true),
    /**
     * Weather Snow should break crops
     */
    SNOW_BREAKS_CROPS("Farming.Weak Crops.Snow Breaks Crops", VarType.BOOLEAN, true),
    /**
     * Should you be able to craft melonseeds
     */
    CANT_CRAFT_MELONSEEDS("Farming.Cant Craft Melonseeds", VarType.BOOLEAN, true),
    /**
     * whether bonemeal may be used on mushrooms
     */
    NO_BONEMEAL_ON_MUSHROOMS("Farming.No Bonemeal On Mushrooms", VarType.BOOLEAN, true),
    /**
     * whether nether wart will ever drop more than 1 wart when broken
     */
    NO_FARMING_NETHER_WART("Farming.No Farming Nether Wart", VarType.BOOLEAN, true),
    /**
     * whether sheep will always regrow white wool
     */
    SHEEP_REGROW_WHITE_WOOL("Farming.Sheep Grow Only White Wool", VarType.BOOLEAN, true),
    /**
     * whether squid will only be able to spawn in ocean biomes
     */
    SQUID_ONLY_SPAWN_IN_OCEAN("Farming.Squid Only Spawn In Ocean", VarType.BOOLEAN, true),
    /**
     * whether players may move water source blocks
     */
    DONT_MOVE_WATER_SOURCE_BLOCKS("Farming.Buckets Dont Move Water Sources", VarType.BOOLEAN, true),
    /**
     * wheter animals should drop exp
     */
    ANIMAL_EXP_NERF("Farming.Animal Experience Nerf", VarType.BOOLEAN, true),
    /**
     * Disable drops from Iron Golems, especially iron?
     */
    IRON_GOLEM_NERF("Farming.Iron Golem Nerf", VarType.BOOLEAN, true),
    /**
     * Prevent animal overcrowding on a small area
     */
    ANIMAL_OVERCROWD_CONTROL("Farming.Animal Overcrowding Control.Enable", VarType.BOOLEAN, true),
    /**
     * Threshold/Number of animals before start damaging animals
     */
    ANIMAL_OVERCROWD_THRESHOLD("Farming.Animal Overcrowding Control.Threshold", VarType.INTEGER, SubType.NATURAL_NUMBER, 10, 
            "Maximum amount of animals allowed in a small area before they start dying"),
    /**
     * #############################
     * # ADDITIONAL FALLING BLOCKS #
     * #############################
     */
    /**
     * Wheter More Falling blocks should be enabled
     */
    MORE_FALLING_BLOCKS_ENABLE("Additional Falling Blocks.Enable", VarType.BOOLEAN, true),
    /**
     * Should Falling Blocks break torches when they land
     */
    MORE_FALLING_BLOCKS_BREAK_TORCHES("Additional Falling Blocks.Break Torches", VarType.BOOLEAN, true),
    /**
     * Whether a falling block that is broken by an obstructing block should drop as an item
     */
    MORE_FALLING_BLOCKS_DROP_ITEM("Additional Falling Blocks.Drop As Items", VarType.BOOLEAN, false,
            "Whether a falling block that is broken by an obstructing block should drop as an item"),
    /**
     * Cascading falling blocks
     */
    MORE_FALLING_BLOCKS_CASCADE("Additional Falling Blocks.Landed Blocks Can Cause Blocks To Fall", VarType.BOOLEAN, true,
            "When a falling block lands it checks if the blocks around it should fall as well. Can cascade downwards infinitely."),
    /**
     * How much damage loose Falling Logs do to Players and Animals
     */
    MORE_FALLING_BLOCKS_DMG_AMOUNT("Additional Falling Blocks.Dmg Amount When Hitting Players", VarType.INTEGER, SubType.NATURAL_NUMBER, 2,
            "Should a falling block damage players when it lands on them."),
    /**
     * wheter falling grass/mycel turns into dirt
     */
    MORE_FALLING_BLOCKS_TURN_TO_DIRT("Additional Falling Blocks.Turn Mycel/Grass To Dirt", VarType.BOOLEAN, true),
    /**
     * which materials beyond sand and gravel should be subject to gravity
     */
    MORE_FALLING_BLOCKS("Additional Falling Blocks.Enabled Blocks", VarType.BLOCKTYPE_LIST, new DefaultFallingBlocks()),

    /**
     * ##############################
     * # GENERAL EXPLOSION SETTINGS #
     * ##############################
     */
    /**
     * Should Stone be turned to cobblestone
     */
    EXPLOSIONS_TURN_STONE_TO_COBLE("Explosions.Turn Stone To Cobble", VarType.BOOLEAN, true,
            "When enabled explosions will turn surrounding stone into cobblestone "),
    /**
     * #####################
     * # EXPLOSION PHYSICS #
     * #####################
     */
    /**
     * Enable cool flying blocks
     */
    EXPLOSIONS_FYLING_BLOCKS_ENABLE("Explosions.Physics.Enable", VarType.BOOLEAN, true,
            "Makes explosions uber cool by throwing blocks up into the air"),
    /**
     * If explosions from other plugins should also be affected (disabled by default)
     */
    EXPLOSIONS_FYLING_BLOCKS_ENABLE_OTHER("Explosions.Physics.Enable For Plugin Created Explosions", VarType.BOOLEAN, false),
    /**
     * How many blocks will go flying
     */
    EXPLOSIONS_FLYING_BLOCKS_PERCENTAGE("Explosions.Physics.Blocks Affected Percentage", VarType.INTEGER, SubType.PERCENTAGE, 20,
            "How many of the blocks that would have been destroyed should go flying instead"),
    /**
     * How fast the blocks accelerate upwards
     */
    EXPLOSIONS_FLYING_BLOCKS_UP_VEL("Explosions.Physics.Up Velocity", VarType.DOUBLE, 2.0,
            "Following 2 variables basically determine the angle and speed in what the blocks go flying"),
    /**
     * How far the blocks spread
     */
    EXPLOSIONS_FLYING_BLOCKS_SPREAD_VEL("Explosions.Physics.Spread Velocity", VarType.DOUBLE, 3.0),
    /**
     * In what radius the flying blocks shouldnt be placed
     */
    EXPLOSIONS_FLYING_BLOCKS_AUTOREMOVE_RADIUS("Explosions.Physics.Exceed Radius Autoremove", VarType.INTEGER, SubType.NATURAL_NUMBER, 10,
            "Blocks exceeding this radius will no be placed in the world to avoid explosions uglying the landscape.",
            "Set to 0 if you want blocks to not be placed at all"),
    /**
     * This determines if the explosion is categorized as under or above
     */
    EXPLOSIONS_Y("Explosions.Border Y", VarType.INTEGER, SubType.NATURAL_NUMBER, 55,
            "Determines where your surface is located. You can have seperate settings for the surface and caves."),

    //WHEN ADDING NEW EXPLOSIONTYPES YOU HAVE TO ADD THE NODES TO EXPLOSIONTYPE AND ALSO UPDATE THE EXPLOSIONTASK
    /**
     * CREEPER Enable this custom explosion
     */
    EXPLOSIONS_CREEPERS_ENABLE("Explosions.Creeper.Enable Custom Explosion", VarType.BOOLEAN, true),
    /**
     * Size of Explosion below border
     */
    EXPLOSIONS_CREEPERS_BELOW_POWER("Explosions.Creeper.Below Border.Explosion Power", VarType.INTEGER, SubType.NATURAL_NUMBER, 3,
            "3 = default creeper, 4 = default tnt, 6 = default charged creeper"),
    /**
     * Set Fire on Explosion below border
     */
    EXPLOSIONS_CREEPERS_BELOW_FIRE("Explosions.Creeper.Below Border.Set Fire", VarType.BOOLEAN, false),
    /**
     * Damage the world below border
     */
    EXPLOSIONS_CREEPERS_BELOW_WORLD_GRIEF("Explosions.Creeper.Below Border.World Damage", VarType.BOOLEAN, true),
    /**
     * Size of Explosion below border
     */
    EXPLOSIONS_CREEPERS_ABOVE_POWER("Explosions.Creeper.Above Border.Explosion Power", VarType.INTEGER, SubType.NATURAL_NUMBER, 3),
    /**
     * Set Fire on Explosion below border
     */
    EXPLOSIONS_CREEPERS_ABOVE_FIRE("Explosions.Creeper.Above Border.Set Fire", VarType.BOOLEAN, false),
    /**
     * Damage the world below border
     */
    EXPLOSIONS_CREEPERS_ABOVE_WORLD_GRIEF("Explosions.Creeper.Above Border.World Damage", VarType.BOOLEAN, true,
            "Disabling worlddamage allows you to have explosions that damage players above ground, but doesn't make a mess."),

    /**
     * Charged CREEPER Enable?
     */
    EXPLOSIONS_CHARGED_CREEPERS_ENABLE("Explosions.Charged Creeper.Enable Custom Explosion", VarType.BOOLEAN, true),
    /**
     * Size of Explosion below the border
     */
    EXPLOSIONS_CHARGED_CREEPERS_BELOW_POWER("Explosions.Charged Creeper.Below Border.Explosion Power", VarType.INTEGER, SubType.NATURAL_NUMBER, 4),
    /**
     * Set Fire on Explosion below border
     */
    EXPLOSIONS_CHARGED_CREEPERS_BELOW_FIRE("Explosions.Charged Creeper.Below Border.Set Fire", VarType.BOOLEAN, false),
    /**
     * Damage the world below border
     */
    EXPLOSIONS_CHARGED_CREEPERS_BELOW_WORLD_GRIEF("Explosions.Charged Creeper.Below Border.World Damage", VarType.BOOLEAN, true),
    /**
     * Size of Explosion below border
     */
    EXPLOSIONS_CHARGED_CREEPERS_ABOVE_POWER("Explosions.Charged Creeper.Above Border.Explosion Power", VarType.INTEGER, SubType.NATURAL_NUMBER, 4),
    /**
     * Set Fire on Explosion below border
     */
    EXPLOSIONS_CHARGED_CREEPERS_ABOVE_FIRE("Explosions.Charged Creeper.Above Border.Set Fire", VarType.BOOLEAN, false),
    /**
     * Damage the world below border
     */
    EXPLOSIONS_CHARGED_CREEPERS_ABOVE_WORLD_GRIEF("Explosions.Charged Creeper.Above Border.World Damage", VarType.BOOLEAN, true),

    /**
     * TNT Enable Custom Explosion?
     */
    EXPLOSIONS_TNT_ENABLE("Explosions.Tnt.Enable Custom Explosion", VarType.BOOLEAN, true),
    /**
     * whether TNT should explode multiple times
     */
    BETTER_TNT("Explosions.Tnt.Enable Multiple Explosions", VarType.BOOLEAN, true,
            "Creates 3 explosions at random locations close to the original tnt",
            "Makes for more natural looking craters."),
    /**
     * wheter the crafting recipe should give more tnt
     */
    MORE_TNT_NUMBER("Explosions.Tnt.Tnt Per Recipe", VarType.INTEGER, SubType.NATURAL_NUMBER, Disable.ONE, 3,
            "Change recipe to yield 3 tnt instead of 1"),
    /**
     * Size of Explosion below the border
     */
    EXPLOSIONS_TNT_BELOW_POWER("Explosions.Tnt.Below Border.Explosion Power", VarType.INTEGER, SubType.NATURAL_NUMBER, 5),
    /**
     * Set Fire on Explosion below border
     */
    EXPLOSIONS_TNT_BELOW_FIRE("Explosions.Tnt.Below Border.Set Fire", VarType.BOOLEAN, false),
    /**
     * Damage the world below border
     */
    EXPLOSIONS_TNT_BELOW_WORLD_GRIEF("Explosions.Tnt.Below Border.World Damage", VarType.BOOLEAN, true),
    /**
     * Size of Explosion below border
     */
    EXPLOSIONS_TNT_ABOVE_POWER("Explosions.Tnt.Above Border.Explosion Power", VarType.INTEGER, SubType.NATURAL_NUMBER, 3),
    /**
     * Set Fire on Explosion below border
     */
    EXPLOSIONS_TNT_ABOVE_FIRE("Explosions.Tnt.Above Border.Set Fire", VarType.BOOLEAN, false),
    /**
     * Damage the world below border
     */
    EXPLOSIONS_TNT_ABOVE_WORLD_GRIEF("Explosions.Tnt.Above Border.World Damage", VarType.BOOLEAN, true),

    /**
     * BLAZE whether blazes explode and spread fire when they die
     */
    BLAZES_EXPLODE_ON_DEATH("Explosions.Blazes Explode On Death.Enable", VarType.BOOLEAN, true),
    /**
     * Size of Explosion below the border
     */
    EXPLOSIONS_BLAZE_BELOW_POWER("Explosions.Blazes Explode On Death.Below Border.Explosion Power", VarType.INTEGER, SubType.NATURAL_NUMBER, 4),
    /**
     * Set Fire on Explosion below border
     */
    EXPLOSIONS_BLAZE_BELOW_FIRE("Explosions.Blazes Explode On Death.Below Border.Set Fire", VarType.BOOLEAN, true),
    /**
     * Damage the world below border
     */
    EXPLOSIONS_BLAZE_BELOW_WORLD_GRIEF("Explosions.Blazes Explode On Death.Below Border.World Damage", VarType.BOOLEAN, true),
    /**
     * Size of Explosion below border
     */
    EXPLOSIONS_BLAZE_ABOVE_POWER("Explosions.Blazes Explode On Death.Above Border.Explosion Power", VarType.INTEGER, SubType.NATURAL_NUMBER, 4),
    /**
     * Set Fire on Explosion below border
     */
    EXPLOSIONS_BLAZE_ABOVE_FIRE("Explosions.Blazes Explode On Death.Above Border.Set Fire", VarType.BOOLEAN, true),
    /**
     * Damage the world below border
     */
    EXPLOSIONS_BLAZE_ABOVE_WORLD_GRIEF("Explosions.Blazes Explode On Death.Above Border.World Damage", VarType.BOOLEAN, true),

    /**
     * Ghast Enable custom Explosion?
     */
    EXPLOSIONS_GHASTS_ENABLE("Explosions.Ghasts.Enable Custom Explosion", VarType.BOOLEAN, true),
    /**
     * Size of Explosion below the border
     */
    EXPLOSIONS_GHAST_BELOW_POWER("Explosions.Ghasts.Below Border.Explosion Power", VarType.INTEGER, SubType.NATURAL_NUMBER, 2),
    /**
     * Set Fire on Explosion below border
     */
    EXPLOSIONS_GHAST_BELOW_FIRE("Explosions.Ghasts.Below Border.Set Fire", VarType.BOOLEAN, true),
    /**
     * Damage the world below border
     */
    EXPLOSIONS_GHAST_BELOW_WORLD_GRIEF("Explosions.Ghasts.Below Border.World Damage", VarType.BOOLEAN, true),
    /**
     * Size of Explosion below border
     */
    EXPLOSIONS_GHAST_ABOVE_POWER("Explosions.Ghasts.Above Border.Explosion Power", VarType.INTEGER, SubType.NATURAL_NUMBER, 2),
    /**
     * Set Fire on Explosion below border
     */
    EXPLOSIONS_GHAST_ABOVE_FIRE("Explosions.Ghasts.Above Border.Set Fire", VarType.BOOLEAN, true),
    /**
     * Damage the world below border
     */
    EXPLOSIONS_GHAST_ABOVE_WORLD_GRIEF("Explosions.Ghasts.Above Border.World Damage", VarType.BOOLEAN, true),;

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
     * Subtype like percentage, y-value, health
     */
    private SubType subType = null;

    /**
     * Default value.
     */
    private final Object defaultValue;

    /**
     * The value that will disable this option
     */
    private Disable disableValue = null;


    /**
     * Constructor.
     *
     * @param path - Configuration path.
     * @param type - Variable type.
     * @param def  - Default value.
     */
    private RootNode(String path, VarType type, Object def)
    {
        this.comments = null;
        this.path = path;
        this.type = type;
        this.defaultValue = def;
    }


    /**
     * Constructor.
     *
     * @param path     - Configuration path.
     * @param type     - Variable type.
     * @param def      - Default value.
     * @param comments - Explaining this node
     */
    private RootNode(String path, VarType type, Object def, String... comments)
    {
        this.path = path;
        this.type = type;
        this.defaultValue = def;
        this.comments = comments;
    }


    private RootNode(String path, VarType type, SubType subType, Object def)
    {
        this.comments = null;
        this.path = path;
        this.type = type;
        this.defaultValue = def;
        this.subType = subType;
    }


    private RootNode(String path, VarType type, SubType subType, Object def, String... comments)
    {
        this.path = path;
        this.type = type;
        this.defaultValue = def;
        this.subType = subType;
        this.comments = comments;
    }


    private RootNode(String path, VarType type, SubType subType, Disable disable, Object def)
    {
        this.comments = null;
        this.path = path;
        this.type = type;
        this.defaultValue = def;
        this.subType = subType;
        this.disableValue = disable;
    }


    private RootNode(String path, VarType type, SubType subType, Disable disable, Object def, String... comments)
    {
        this.comments = comments;
        this.path = path;
        this.type = type;
        this.defaultValue = def;
        this.subType = subType;
        this.disableValue = disable;
    }


    /**
     * Comment Constructor
     */
    private RootNode(String path, String... comments)
    {
        this.path = path;
        this.comments = comments;
        this.type = VarType.COMMENT;
        this.defaultValue = null;
        this.subType = null;
        this.disableValue = null;
    }


    @Override
    public String getPath()
    {
        return baseNode() + "." + path;
    }


    @Override
    public VarType getVarType()
    {
        return type;
    }


    @Override
    public Object getDefaultValue()
    {
        return defaultValue;
    }


    @Override
    public SubType getSubType()
    {
        return subType;
    }


    public static String baseNode()
    {
        return "ExtraHardMode";
    }


    /**
     * Get comment describing this node
     *
     * @return comment or null if not set
     */
    public String[] getComments()
    {
        return comments;
    }


    /**
     * Get the Object that will disable this option
     * <pre>Defaults:
     * boolean: false
     * int:
     *   no SubType set: 0
     *   no Disable value: 0
     *   health: 20
     *   percentage: 0
     * double:
     *   same as int
     * string: ""
     *   subtype and disable ignored
     * list: .emptyList()
     * blocktypelist: same as list
     * </pre>
     *
     * @return Object that will disable this option in the plugin
     */
    @Override
    public Object getValueToDisable()
    {
        Object obj;
        switch (type)
        {
            case BOOLEAN:
            {
                obj = false;
                break;
            }
            case INTEGER:
            {
                obj = 0;
                if (subType != null)
                {
                    if (disableValue != null && disableValue.get() != null)
                        obj = disableValue.get();
                    else
                    {
                        switch (subType)
                        {
                            case NATURAL_NUMBER:
                            case Y_VALUE:
                            {
                                obj = 0;
                                break;
                            }
                            case HEALTH:
                            {
                                obj = 20;
                                break;
                            }
                            case PERCENTAGE:
                            {
                                obj = 0;
                                break;
                            }
                            default:
                            {
                                obj = defaultValue;
                                throw new UnsupportedOperationException("SubType hasn't been specified for " + path);
                            }
                        }
                    }
                }
                break;
            }
            case DOUBLE:
            {
                obj = 0.0;
                if (subType != null)
                    if (disableValue != null && disableValue.get() != null)
                        obj = disableValue.get();
                    else
                    {
                        switch (subType)
                        {
                            case NATURAL_NUMBER:
                            case Y_VALUE:
                            {
                                if (disableValue != null)
                                    obj = (Double) disableValue.get();
                                break;
                            }
                            case HEALTH:
                            {
                                obj = 20.0;
                                break;
                            }
                            case PERCENTAGE:
                            {
                                obj = 0.0;
                                break;
                            }
                            default:
                            {
                                obj = defaultValue;
                                throw new UnsupportedOperationException("SubType hasn't been specified for " + path);
                            }
                        }
                    }
                break;
            }
            case STRING:
            {
                obj = "";
                break;
            }
            case LIST:
            {
                obj = Collections.emptyList();
                break;
            }
            case BLOCKTYPE_LIST:
            {
                obj = BlockTypeList.EMPTY_LIST;
                break;
            }
            case BLOCK_RELATION_LIST:
            {
                obj = BlockRelationsList.EMPTY_LIST;
                break;
            }
            case POTION_EFFECT:
            {
                return null;
            }
            default:
            {
                throw new UnsupportedOperationException("Type of " + type + " doesn't have a default value to be disabled");
            }
        }
        return obj;
    }


    /**
     * Contains values for some Nodes which require a special value, which differs from other Nodes with the same type
     */
    private enum Disable
    {
        /**
         * A value of 0 will disable this feature in the plugin
         */
        ZERO(0),
        /**
         * A value of 1 will disable this feature in the plugin
         */
        ONE(1),
        /**
         * A value of 100 (as in percentage will disable this feature)
         */
        HUNDRED(100);


        private Disable(Object obj)
        {
            disable = obj;
        }


        final Object disable;


        public Object get()
        {
            return disable;
        }
    }


    /**
     * Default list of falling blocks.
     */
    private static class DefaultFallingBlocks extends BlockTypeList
    {
        /**
         * Constructor.
         */
        public DefaultFallingBlocks()
        {
            super();
            this.add(new BlockType(Material.DIRT));
            this.add(new BlockType(Material.GRASS));
            this.add(new BlockType(Material.COBBLESTONE));
            this.add(new BlockType(Material.MOSSY_COBBLESTONE));
            this.add(new BlockType(Material.DOUBLE_STEP, (short) 3)); //cobble double halfslabs
            this.add(new BlockType(Material.STEP, (short) 3)); //normal
            this.add(new BlockType(Material.STEP, (short) 11)); //upside
            this.add(new BlockType(Material.MYCEL));
        }
    }


    /**
     * Default list of falling blocks.
     */
    private static class DefaultPhysicsBlocks extends BlockTypeList
    {
        /**
         * Constructor.
         */
        public DefaultPhysicsBlocks()
        {
            super();
            this.add(new BlockType(Material.COAL_ORE));
            this.add(new BlockType(Material.IRON_ORE));
            this.add(new BlockType(Material.GOLD_ORE));
            this.add(new BlockType(Material.LAPIS_ORE));
            this.add(new BlockType(Material.REDSTONE_ORE));
            this.add(new BlockType(Material.GLOWING_REDSTONE_ORE));
            this.add(new BlockType(Material.EMERALD_ORE));
            this.add(new BlockType(Material.DIAMOND_ORE));
        }
    }


    /**
     * Default list of falling blocks.
     */
    private static class DefaultToolDurabilities extends BlockTypeList
    {
        /**
         * Constructor.
         */
        public DefaultToolDurabilities()
        {
            super();
            this.add(new BlockType(Material.IRON_PICKAXE, (short) 32));
            this.add(new BlockType(Material.DIAMOND_PICKAXE, (short) 64));
        }
    }


    /**
     * Default stone cobble relations
     */
    private static class DefaultStoneBlocks extends BlockRelationsList
    {
        /**
         * Constructor.
         */
        public DefaultStoneBlocks()
        {
            super();
            this.add(new BlockType(Material.STONE), new BlockType(Material.COBBLESTONE));
        }
    }


    private static class DefaultValuableTools extends BlockTypeList
    {
        public DefaultValuableTools()
        {
            super();
            this.add(new BlockType(Material.DIAMOND_AXE));
            this.add(new BlockType(Material.DIAMOND_SWORD));
            this.add(new BlockType(Material.DIAMOND_PICKAXE));
            this.add(new BlockType(Material.DIAMOND_SPADE));

        }
    }


    private static class DefaultHardBlocks extends BlockTypeList
    {
        public DefaultHardBlocks()
        {
            super();
            this.add(new BlockType(Material.STONE));

        }
    }


    private static class DefaultWorlds extends ArrayList<String>
    {
        public DefaultWorlds()
        {
            super();
            this.add(MultiWorldConfig.ALL_WORLDS);
        }
    }
}
