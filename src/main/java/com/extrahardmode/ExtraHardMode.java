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


package com.extrahardmode;


import com.extrahardmode.command.Commander;
import com.extrahardmode.compatibility.CompatHandler;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.config.messages.MessageConfig;
import com.extrahardmode.config.messages.MessageNode;
import com.extrahardmode.features.AnimalCrowdControl;
import com.extrahardmode.features.AntiFarming;
import com.extrahardmode.features.AntiGrinder;
import com.extrahardmode.features.DebugMode;
import com.extrahardmode.features.Explosions;
import com.extrahardmode.features.HardenedStone;
import com.extrahardmode.features.LimitedBuilding;
import com.extrahardmode.features.MoreTnt;
import com.extrahardmode.features.Physics;
import com.extrahardmode.features.Players;
import com.extrahardmode.features.Torches;
import com.extrahardmode.features.Tutorial;
import com.extrahardmode.features.Water;
import com.extrahardmode.features.monsters.Blazes;
import com.extrahardmode.features.monsters.BumBumBens;
import com.extrahardmode.features.monsters.CaveSpider;
import com.extrahardmode.features.monsters.Endermen;
import com.extrahardmode.features.monsters.Ghasts;
import com.extrahardmode.features.monsters.Glydia;
import com.extrahardmode.features.monsters.Guardians;
import com.extrahardmode.features.monsters.Horses;
import com.extrahardmode.features.monsters.KillerBunny;
import com.extrahardmode.features.monsters.MonsterRules;
import com.extrahardmode.features.monsters.PigMen;
import com.extrahardmode.features.monsters.Silverfish;
import com.extrahardmode.features.monsters.Skeletors;
import com.extrahardmode.features.monsters.Spiders;
import com.extrahardmode.features.monsters.Vex;
import com.extrahardmode.features.monsters.Vindicator;
import com.extrahardmode.features.monsters.Witches;
import com.extrahardmode.features.monsters.Zombies;
import com.extrahardmode.metrics.ConfigPlotter;
import com.extrahardmode.module.BlockModule;
import com.extrahardmode.module.DataStoreModule;
import com.extrahardmode.module.ExplosionCompatStorage;
import com.extrahardmode.module.MsgModule;
import com.extrahardmode.module.MsgPersistModule;
import com.extrahardmode.module.PlayerModule;
import com.extrahardmode.module.UtilityModule;
import com.extrahardmode.module.temporaryblock.TemporaryBlockHandler;
import com.extrahardmode.service.IModule;
import com.extrahardmode.service.OurRandom;
import com.extrahardmode.task.MoreMonstersTask;
import com.extrahardmode.task.WeightCheckTask;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;


/**
 * Main plugin class.
 */
public class ExtraHardMode extends JavaPlugin
{

    /**
     * Plugin tag.
     */
    public static String TAG = "[EHM]";

    /**
     * Registered modules.
     */
    private final Map<Class<? extends IModule>, IModule> modules = new LinkedHashMap<Class<? extends IModule>, IModule>();

    /**
     * for computing random chance
     */
    private final Random randomNumberGenerator = new Random();


    /**
     * initializes well... everything
     */
    @Override
    public void onEnable()
    {
        // Register modules
        registerModule(RootConfig.class, new RootConfig(this));
        MessageConfig config = new MessageConfig(this);
        registerModule(MessageConfig.class, config);
        String prefix = config.getString(MessageNode.EHM_CHAT_PREFIX);
        if (prefix != null)
            TAG = prefix;

        File rootFolder = new File(getDataFolder().getPath() + File.separator + "persistence" + File.separator);
        rootFolder.mkdirs();
        registerModule(MsgPersistModule.class, new MsgPersistModule(this, rootFolder + File.separator + "messages_count.db"));

        registerModule(MsgModule.class, new MsgModule(this));

        registerModule(DataStoreModule.class, new DataStoreModule(this));
        registerModule(BlockModule.class, new BlockModule(this));
        registerModule(UtilityModule.class, new UtilityModule(this));
        registerModule(PlayerModule.class, new PlayerModule(this));

        //Register command
        getCommand("ehm").setExecutor(new Commander(this));

        //Basic Modules
        registerModule(AntiFarming.class, new AntiFarming(this));
        registerModule(AnimalCrowdControl.class, new AnimalCrowdControl(this));
        registerModule(AntiGrinder.class, new AntiGrinder(this));
        registerModule(DebugMode.class, new DebugMode(this));
        registerModule(Explosions.class, new Explosions(this));
        registerModule(HardenedStone.class, new HardenedStone(this));
        registerModule(LimitedBuilding.class, new LimitedBuilding(this));
        registerModule(MoreTnt.class, new MoreTnt(this));
        registerModule(Physics.class, new Physics(this));
        registerModule(Players.class, new Players(this));
        registerModule(Torches.class, new Torches(this));
        registerModule(Water.class, new Water(this));

        //Utils
        registerModule(TemporaryBlockHandler.class, new TemporaryBlockHandler(this));

        //Monster Modules
        registerModule(Blazes.class, new Blazes(this));
        registerModule(BumBumBens.class, new BumBumBens(this));
        registerModule(Endermen.class, new Endermen(this));
        registerModule(Glydia.class, new Glydia(this));
        registerModule(Ghasts.class, new Ghasts(this));
        try //Enabled from 1.6 onwards only
        {
            Class.forName("org.bukkit.entity.Horse", false, null);
            registerModule(Horses.class, new Horses(this));
        } catch (ClassNotFoundException ignored)
        {
        }
        registerModule(MonsterRules.class, new MonsterRules(this));
        registerModule(PigMen.class, new PigMen(this));
        //registerModule(RealisticChopping.class, new RealisticChopping(this));
        registerModule(Silverfish.class, new Silverfish(this));
        registerModule(Skeletors.class, new Skeletors(this));
        registerModule(Spiders.class, new Spiders(this));
        registerModule(Zombies.class, new Zombies(this));
        registerModule(Witches.class, new Witches(this));
        registerModule(KillerBunny.class, new KillerBunny(this));
        registerModule(Vindicator.class, new Vindicator(this));
        registerModule(CaveSpider.class, new CaveSpider(this));
        registerModule(Guardians.class, new Guardians(this));
        registerModule(Vex.class, new Vex(this));
        
        //Compatibility
        registerModule(CompatHandler.class, new CompatHandler(this));
        registerModule(ExplosionCompatStorage.class, new ExplosionCompatStorage(this));

        //TODO make modules
        registerModule(Tutorial.class, new Tutorial(this));

        OurRandom.reload();

        // FEATURE: monsters spawn in the light under a configurable Y level
        MoreMonstersTask task = new MoreMonstersTask(this);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, task, 600L, 600L);

        //Feature: check weight task if no swimming in armor active and feature active in at least one world
        boolean active = false;
        for (World world : getServer().getWorlds())
            if (getModuleForClass(RootConfig.class).getBoolean(RootNode.NO_SWIMMING_IN_ARMOR, world.getName()))
                active = true;
        if (active)
            this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new WeightCheckTask(this), 20L * 5, 20L * 5);

        //Armor task
        /*
        active = false;
        for (World world : getServer().getWorlds())
            if (getModuleForClass(RootConfig.class).getBoolean(RootNode.ARMOR_SLOWDOWN_ENABLE, world.getName()))
                active = true;
        if (active)
            this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ArmorWeightTask(this), 20L * 5, 20L * 3);
        */
        //Metrics Plotter, this gets included by maven
        new ConfigPlotter(this, getModuleForClass(RootConfig.class));
    }

    public void debug(World world, String message)
    {
        if ((getModuleForClass(RootConfig.class)).getBoolean(RootNode.DEBUG, world.getName()))
            this.getLogger().info(message);
    }


    @Override
    public void onDisable()
    {
        super.onDisable();
        //Gracefully stop all modules
        for (IModule module : modules.values())
            module.closing();
        for (Player player : getServer().getOnlinePlayers())
            player.setWalkSpeed(0.2F);
        this.getServer().getScheduler().cancelTasks(this);
        modules.clear();
    }


    /**
     * Computes random chance
     *
     * @param percentChance - Percentage of success.
     *
     * @return True if it was successful, else false.
     */
    public boolean random(int percentChance)
    {
        return randomNumberGenerator.nextInt(101) < percentChance;
    }


    /**
     * Get random generator.
     *
     * @return a Random object
     */
    public Random getRandom()
    {
        return randomNumberGenerator;
    }


    public String getTag()
    {
        return TAG;
    }


    /**
     * Register a module.
     *
     * @param clazz  - Class of the instance.
     * @param module - Module instance.
     *
     * @throws IllegalArgumentException - Thrown if an argument is null.
     */
    <T extends IModule> void registerModule(Class<T> clazz, T module)
    {
        // Check arguments.
        if (clazz == null)
        {
            throw new IllegalArgumentException("Class cannot be null");
        } else if (module == null)
        {
            throw new IllegalArgumentException("Module cannot be null");
        }
        // Add module.
        modules.put(clazz, module);
        // Tell module to start.
        module.starting();
    }


    /**
     * Deregister a module.
     *
     * @param clazz - Class of the instance.
     *
     * @return Module that was removed. Returns null if no instance of the module is registered.
     */
    public <T extends IModule> T deregisterModuleForClass(Class<T> clazz)
    {
        // Check arguments.
        if (clazz == null)
        {
            throw new IllegalArgumentException("Class cannot be null");
        }
        // Grab module and tell it its closing.
        T module = clazz.cast(modules.get(clazz));
        if (module != null)
        {
            module.closing();
        }
        return module;
    }


    /**
     * Retrieve a registered module.
     *
     * @param clazz - Class identifier.
     *
     * @return Module instance. Returns null is an instance of the given class has not been registered with the API.
     */
    public <T extends IModule> T getModuleForClass(Class<T> clazz)
    {
        return clazz.cast(modules.get(clazz));
    }


    /**
     * Get all the registered modules
     *
     * @return Map of modules
     */
    public Map<Class<? extends IModule>, IModule> getModules()
    {
        return modules;
    }

    /**
     * Determines if a config node is enabled in any world
     * @param node
     * @return True if enabled in a world, false if disabled everywhere
     */
    public boolean isNodeEnabled(RootNode node)
    {
        for (World world : getServer().getWorlds())
            if (getModuleForClass(RootConfig.class).getBoolean(node, world.getName()))
                return true;
        return false;
    }
}
