package com.extrahardmode.features;


import com.extrahardmode.ExtraHardMode;
import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.module.PlayerModule;
import com.extrahardmode.module.UtilityModule;
import com.extrahardmode.service.Feature;
import com.extrahardmode.service.ListenerModule;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;

/** @author Diemex */
public class MoreTnt extends ListenerModule
{
    private RootConfig CFG;

    private PlayerModule playerModule;


    public MoreTnt(ExtraHardMode plugin)
    {
        super(plugin);
    }


    @Override
    public void starting()
    {
        super.starting();
        CFG = plugin.getModuleForClass(RootConfig.class);
        playerModule = plugin.getModuleForClass(PlayerModule.class);
    }


    /** Gets called just when an ItemStack is about to be crafted Sets the amount in the result slot to the appropriate number */
    @EventHandler
    public void beforeCraft(PrepareItemCraftEvent event)
    {
        Inventory inv = event.getInventory();

        if (inv != null && inv.getHolder() != null)
        {
            InventoryHolder human = inv.getHolder();
            if (human instanceof Player)
            {
                Player player = (Player) human;
                World world = player.getWorld();

                final int multiplier = CFG.getInt(RootNode.MORE_TNT_NUMBER, world.getName());

                switch (multiplier)
                {
                    case 0:
                        break;
                    case 1:
                        break;
                    default:
                        if (event.getRecipe() == null) //1.12 will return null I guess
                            return;
                        if (event.getRecipe().getResult().getType().equals(Material.TNT))
                        {
                            //TODO LOW EhmMoreTntEvent
                            //Recipe in CraftingGrid
                            ShapedRecipe craftRecipe = (ShapedRecipe) event.getRecipe();
                            CraftingInventory craftInv = event.getInventory();

                            //The vanilla tnt recipe
                            ShapedRecipe vanillaTnt = new ShapedRecipe(new NamespacedKey(plugin, "moretnt"), new ItemStack(Material.TNT)).shape("gsg", "sgs", "gsg").setIngredient('g', Material.GUNPOWDER).setIngredient('s', Material.SAND);

                            //Multiply the amount of tnt in enabled worlds
                            if (UtilityModule.isSameRecipe(craftRecipe, vanillaTnt))
                            {
                                craftInv.setResult(new ItemStack(Material.TNT, multiplier));
                            }
                        }
                        break;
                }
            }
        }
    }


    /**
     * when a player crafts something... MoreTnt: Contains the logic for modifying the amount of tnt crafted on a per world basis.
     *
     * @param event - Event that occurred.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onItemCrafted(CraftItemEvent event)
    {
        InventoryHolder human = event.getInventory().getHolder();

        if (human instanceof Player)
        {
            Player player = (Player) human;

            World world = player.getWorld();

            final int multiplier = CFG.getInt(RootNode.MORE_TNT_NUMBER, world.getName());
            final boolean playerBypasses = playerModule.playerBypasses(player, Feature.EXPLOSIONS);

            //Are we crafting tnt and is more tnt enabled, from BeforeCraftEvent
            if (event.getRecipe().getResult().equals(new ItemStack(Material.TNT, multiplier)) && !playerBypasses)
            {
                switch (multiplier)
                {
                    case 0:
                        event.setCancelled(true); //Feature disable tnt crafting
                        break;
                    default:
                        Validate.isTrue(multiplier > 0, "Multiplier for tnt can't be negative");
                        PlayerInventory inv = player.getInventory();
                        //ShiftClick only causes this event to be called once
                        if (event.isShiftClick())
                        {
                            int amountBefore = PlayerModule.countInvItem(inv, Material.TNT);
                            //Add the missing tnt 1 tick later, we count what has been added by shiftclicking and multiply it
                            UtilityModule.AddExtraItemsLater task = new UtilityModule.AddExtraItemsLater(inv, amountBefore, Material.TNT, multiplier - 1);
                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, task, 1L);
                        }
                        break;
                }
            }
        }
    }
}
