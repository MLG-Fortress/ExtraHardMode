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
import com.extrahardmode.service.EHMModule;
import com.extrahardmode.service.OurRandom;
import org.apache.commons.lang.Validate;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Map;

/**
 * Put all the Utility Stuff here that doesn't fit into the other modules
 */
@SuppressWarnings("SameParameterValue")
public class UtilityModule extends EHMModule
{
    /**
     * Constructor.
     *
     * @param plugin - Plugin instance.
     */
    public UtilityModule(ExtraHardMode plugin)
    {
        super(plugin);
    }


    /**
     * Generates a Firework with random colors/velocity and the given Firework Type
     *
     * @param type The type of firework
     */
    public void fireWorkRandomColors(FireworkEffect.Type type, Location location)
    {
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        //Generate the colors
        int rdmInt1 = plugin.getRandom().nextInt(255);
        int rdmInt2 = plugin.getRandom().nextInt(255);
        int rdmInt3 = plugin.getRandom().nextInt(255);
        Color mainColor = Color.fromRGB(rdmInt1, rdmInt2, rdmInt3);

        FireworkEffect fwEffect = FireworkEffect.builder().withColor(mainColor).with(type).build();
        fireworkMeta.addEffect(fwEffect);
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
    }


    /**
     * >>>>>>> dev Damage an item based on the amount of Blocks it can mine
     *
     * @param item   Item to damage
     * @param blocks amount of blocks the item can break
     * @return the damaged Item, can be completely broken
     */
    public static ItemStack damage(ItemStack item, int blocks)
    {
        short maxDurability = item.getType().getMaxDurability();
        Validate.isTrue(maxDurability > 1, "This item is not damageable");
        if (blocks <= 0)
            return item;

        short damagePerBlock = (short) (maxDurability / blocks);
        //Because tooldmg is an int we have to sometimes break the tool twice, I couldn't find the flaw in the first formula so oh well....
        double percent = damagePerBlock > 1 ? (maxDurability % blocks) / (double) maxDurability : ((double) maxDurability / blocks) - damagePerBlock;

        if (damagePerBlock > 0 || percent > 0.0D)
        {
            int durability = item.getDurability();
            durability += damagePerBlock;

            if (OurRandom.nextDouble() < percent)
                durability += (damagePerBlock > 0 ? damagePerBlock : 1);

            item.setDurability((short) durability);
        }
        return item;
    }


    private static boolean isSameShape(ArrayList<ItemStack> recipe1, ArrayList<ItemStack> recipe2)
    {
        //compare recipes
        boolean isSame = true;
        for (int i = 0; i < recipe1.size(); i++)
        {
            if (!recipe1.get(i).getType().equals(recipe2.get(i).getType()))
                isSame = false;
        }
        return isSame;
    }


    public static boolean isSameRecipe(ShapedRecipe recipe1, ShapedRecipe recipe2)
    {
        boolean isSameResult = recipe1.getResult().getAmount() == recipe2.getResult().getAmount()
                && recipe1.getResult().getType().equals(recipe2.getResult().getType());
        boolean isSameShape = isSameShape(recipeToArrayList(recipe1), recipeToArrayList(recipe2));
        return isSameShape && isSameResult;
    }


    private static ArrayList<ItemStack> recipeToArrayList(ShapedRecipe recipe)
    {
        String[] shape = recipe.getShape();
        Map<Character, ItemStack> ingredientMap = recipe.getIngredientMap();

        //Create an ArrayList with the actual recipe based of the shape and ingredientMap
        ArrayList<ItemStack> craftRecipe = new ArrayList<ItemStack>();
        String flatShape = strArrToStr(shape);
        for (int i = 0; i < flatShape.length(); i++)
        {
            char id = flatShape.charAt(i);
            ItemStack itemStack = ingredientMap.get(id);
            //to avoid null pointers, empty slots will be filled with air
            if (itemStack == null) itemStack = new ItemStack(Material.AIR);
            craftRecipe.add(itemStack);
        }
        return craftRecipe;
    }


    private static String strArrToStr(String[] arr)
    {
        StringBuilder builder = new StringBuilder();
        for (String s : arr)
        {
            builder.append(s);
        }
        return builder.toString();
    }


    /**
     * Check the inventory after 1 tick and see how many items have been crafted, then add the amount defined by the multiplier
     */
    public static class AddExtraItemsLater implements Runnable
    {
        int amountBefore = 0;

        int amountToAdd = 0;

        final Material material;

        PlayerInventory inv = null;


        public AddExtraItemsLater(PlayerInventory inventory, int amountBefore, Material toCompare, int amountToAdd)
        {
            this.amountBefore = amountBefore;
            this.amountToAdd = amountToAdd;
            material = toCompare;
            inv = inventory;
        }


        @Override
        public void run()
        {
            int amountAfter = PlayerModule.countInvItem(inv, material);
            int amountToAdd = (amountAfter - amountBefore) * (this.amountToAdd);
            inv.addItem(new ItemStack(material, amountToAdd));
        }
    }


    public static Vector randomDir()
    {
        double x = OurRandom.nextDouble() - 1.0;
        double y = 0.0;
        double z = OurRandom.nextDouble() - 1.0;
        return new Vector(x, y, z);
    }


    public static void moveAway(Entity entity, Location center, double speed)
    {
        Location loc = entity.getLocation();
        double x = loc.getX() - center.getX();
        double y = loc.getY() - center.getY();
        double z = loc.getZ() - center.getZ();
        Vector vel = new Vector(x, y, z).normalize().multiply(speed);
        entity.setVelocity(vel);
    }


    public static void moveUp(Entity entity, double speed)
    {
        Vector velocity = new Vector(0.0, speed, 0.0);//.normalize().multiply(-speed);
        entity.setVelocity(velocity.add(randomDir()));
    }


    @Override
    public void starting()
    {
    }


    @Override
    public void closing()
    {
    }
}
