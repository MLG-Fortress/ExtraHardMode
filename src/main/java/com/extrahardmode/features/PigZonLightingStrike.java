package com.extrahardmode.features;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.PigZombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.LightningStrikeEvent;

import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.module.EntityHelper;
import com.extrahardmode.service.ListenerModule;

public class PigZonLightingStrike extends ListenerModule{
	 
	private RootConfig CFG;
	 
	public PigZonLightingStrike(ExtraHardMode plugin)
    {
        super(plugin);
    }
	
    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
    }


    /**
     * When a lightning strikes
     * <p/>
     * spawn pigmen
     */
    @EventHandler
    public void onLightingStrike(LightningStrikeEvent event)
    {
        LightningStrike strike = event.getLightning();

        Location loc = strike.getLocation();
        World world = loc.getWorld();

        final boolean spawnPigsOnLightning = CFG.getBoolean(RootNode.LIGHTNING_SPAWNS_PIGMEN, world.getName());

        if (spawnPigsOnLightning && EntityHelper.simpleIsLocSafeSpawn(loc))
        {
            int rdm = plugin.getRandom().nextInt(10);
            int amount = 1;
            switch (rdm)
            {
                case 0:
                case 1: //20%
                {
                    amount = 2;
                    break;
                }
                case 2:
                case 3: //20%
                {
                    amount = 3;
                    break;
                }
                default:       //60%
                {
                    amount = 1;
                }
            }
            for (int i = 0; i < amount; i++)
            {
                PigZombie pigZombie = world.spawn(loc, PigZombie.class);
                pigZombie.setAnger(Integer.MAX_VALUE);
            }
        }
    }
}
