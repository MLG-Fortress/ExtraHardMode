package com.extrahardmode.task;


/** @author Diemex */

import com.extrahardmode.module.EntityHelper;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Damage a LivingEntity until it's dead, stop the task once the Entity is dead
 *
 * @author Diemex
 */
public class SlowKillTask extends BukkitRunnable
{
    private final LivingEntity entity;
    private final Plugin plugin;


    public SlowKillTask(LivingEntity entity, Plugin plugin)
    {
        this.entity = entity;
        this.plugin = plugin;
        runTaskTimer(plugin, 0L, 30L);
    }


    @Override
    public void run()
    {
        if (!entity.isDead())
        {
            entity.damage(1);
            EntityHelper.addEnvironmentalDamage(plugin, entity, 1.0);
        } else
            cancel();
    }
}
