package com.extrahardmode.service.config.customtypes;


import com.extrahardmode.service.RegexHelper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * A parser for PotionEffects from our Configuration
 *
 * @author Diemex
 */
public class PotionEffectHolder
{
    public final static String key_effect = "Potion Type", key_duration = "Duration (ticks)", key_amplifier = "Amplifier";
    private PotionEffect bukkitEffect;
    private PotionEffectType bukkitEffectType;
    private int duration = 5, amplifier = 1;   //default values


    public PotionEffectHolder()
    {
    }


    public PotionEffectHolder(PotionEffectType effectType, int duration, int amplifier)
    {
        this.bukkitEffectType = effectType;
        this.duration = duration;
        this.amplifier = amplifier;
    }


    public static PotionEffectType parseEffect(String input)
    {
        if (input == null)
            return null;

        PotionEffectType effect = null;
        //figure out if it's an id or string PotionType
        boolean containsNumbers = RegexHelper.containsNumbers(input), containsLetters = RegexHelper.containsLetters(input);

        if (containsLetters)
            effect = PotionEffectType.getByName(input);
        if (effect == null) //Strip values that are most likely invalid
            effect = PotionEffectType.getByName(RegexHelper.stripEnum(input));
        if (effect == null && containsNumbers)
            effect = PotionEffectType.getById(RegexHelper.parseNumber(input)); //TODO: Fix deprecation

        return effect;
    }


    public PotionEffect toBukkitEffect(boolean ambient)
    {
        if (bukkitEffect == null && bukkitEffectType != null && duration > 0 && amplifier > 0)
            bukkitEffect = new PotionEffect(bukkitEffectType, duration, amplifier, ambient);
        return bukkitEffect;
    }


    public void applyEffect(LivingEntity entity, boolean ambient)
    {
        if (toBukkitEffect(ambient) != null)
            entity.addPotionEffect(bukkitEffect);
    }


    /**
     * Load a PotionEffect from disk
     *
     * @param section to load from
     * @return loaded object or null if an error occurred
     */
    public static PotionEffectHolder loadFromConfig(ConfigurationSection section)
    {
        if (section == null)
            return null;
        String effect = section.getString(key_effect, ""),
                length = section.getString(key_duration, ""),
                strength = section.getString(key_amplifier, "");

        if (effect.equals("") || length.equals("") || strength.equals(""))
            return null;

        PotionEffectHolder potionEffect = new PotionEffectHolder();

        potionEffect.setBukkitEffectType(parseEffect(effect));
        potionEffect.setAmplifier(RegexHelper.containsNumbers(strength) ? RegexHelper.parseNumber(strength) : 1);
        potionEffect.setDuration(RegexHelper.containsNumbers(length) ? RegexHelper.parseNumber(length) : 5);

        return potionEffect;
    }


    /**
     * Uses multiple fields to save to config
     *
     * @param section config to write to
     */
    public void saveToConfig(MemorySection section, String parentPath)
    {
        String effectName = bukkitEffectType != null ? bukkitEffectType.getName() : "NONE";
        section.set(parentPath + '.' + key_effect, effectName);
        section.set(parentPath + '.' + key_duration, duration);
        section.set(parentPath + '.' + key_amplifier, amplifier);
    }


    public int getDuration()
    {
        return duration;
    }


    public void setDuration(int duration)
    {
        this.duration = duration;
    }


    public int getAmplifier()
    {
        return amplifier;
    }


    public void setAmplifier(int amplifier)
    {
        this.amplifier = amplifier;
    }


    public PotionEffectType getBukkitEffectType()
    {
        return bukkitEffectType;
    }


    public void setBukkitEffectType(PotionEffectType bukkitEffectType)
    {
        this.bukkitEffectType = bukkitEffectType;
    }
}
