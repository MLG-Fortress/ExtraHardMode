package com.extrahardmode.compatibility;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.service.EHMModule;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles compatibility for all supported plugins in one class
 *
 * @author Diemex
 */
public class CompatHandler extends EHMModule
{
    private static Set<IBlockProtection> blockProtectionPls;

    private static Set<IBlockLogger> blockLoggerPls;

    private static Set<IMonsterProtection> monsterProtectionPls;


    public CompatHandler(ExtraHardMode plugin)
    {
        super(plugin);
    }


    public static boolean isExplosionProtected(Location loc)
    {
        if (loc != null)
            for (IBlockProtection prot : blockProtectionPls)
                if (prot.isExplosionProtected(loc))
                    return true;
        return false;
    }


    public static boolean canMonsterSpawn(Location loc)
    {
        if (loc != null)
            for (IMonsterProtection prot : monsterProtectionPls)
                if (prot.denySpawn(loc))
                    return true;
        return false;
    }


    public static void logFallingBlockFall(Block block)
    {
//        if (block != null)
//            for (IBlockLogger logger : blockLoggerPls)
//                logger.logFallingBlockFall(block);
    }


    public static void logFallingBlockLand(BlockState block)
    {
//        if (block != null)
//            for (IBlockLogger logger : blockLoggerPls)
//                logger.logFallingBlockLand(block);
    }


    @Override
    public void starting()
    {
        blockProtectionPls = new HashSet<IBlockProtection>();
        monsterProtectionPls = new HashSet<IMonsterProtection>();
        blockLoggerPls = new HashSet<IBlockLogger>();

        //BlockLoggers//
        //Prism
//        CompatPrism prismCompat = new CompatPrism(plugin);
//        if (prismCompat.isEnabled())
//            blockLoggerPls.add(prismCompat);

        //HawkEye Reloaded
//        try
//        {
//            CompatHawkEye compatHawkEye = new CompatHawkEye(plugin);
//            if (compatHawkEye.isEnabled())
//                blockLoggerPls.add(compatHawkEye);
//        } catch (Error ignored) //ClassNotFoundException, but that doesn't catch for whatever reason, whateeeeever
//        {
//            //Static API I guess, stop wasting my time any further or you are gonna get removed
//        }
//
        //        //CoreProtect
        //        CompatCoreProtect compatCoreProtect = new CompatCoreProtect(plugin);
        //        if (compatCoreProtect.isEnabled())
        //            blockLoggerPls.add(compatCoreProtect);
    }


    @Override
    public void closing()
    {
        blockProtectionPls = null;
    }
}
