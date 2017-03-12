package com.extrahardmode.metrics;


import com.extrahardmode.config.RootConfig;
import com.extrahardmode.config.RootNode;
import com.extrahardmode.service.config.ConfigNode;
import org.bukkit.plugin.Plugin;

/**
 * Output all the choosen modules to mcstats in nice plots
 *
 * @author Diemex
 */
public class ConfigPlotter
{
    private final Plugin plugin;

    private final RootConfig CFG;


    public ConfigPlotter(Plugin plugin, RootConfig CFG)
    {
        this.plugin = plugin;
        this.CFG = CFG;
        createPlot();
    }


    private void createPlot()
    {

        // Enabled metrics
        Metrics metrics;
        try
        {
            metrics = new Metrics(plugin);

            final int percent = CFG.getEnabledWorlds().length > 0 ? (plugin.getServer().getWorlds().size() * 100 / CFG.getEnabledWorlds().length) : 0;
            Metrics.Graph graph = metrics.createGraph("Enabled for % of worlds");
            graph.addPlotter(
                    new Metrics.Plotter("0-25%")
                    {
                        @Override
                        public int getValue()
                        {
                            return percent < 26 ? 1 : 0;
                        }

                    });
            graph.addPlotter(new Metrics.Plotter("26-50%")
            {
                @Override
                public int getValue()
                {
                    return percent > 25 && percent <= 50 ? 1 : 0;
                }
            });
            graph.addPlotter(new Metrics.Plotter("51-75%")
            {
                @Override
                public int getValue()
                {
                    return percent > 50 && percent <= 75 ? 1 : 0;
                }
            });
            graph.addPlotter(new Metrics.Plotter("76-100%")
            {
                @Override
                public int getValue()
                {
                    return percent > 75 ? 1 : 0;
                }
            });


            for (final RootNode node : RootNode.values())
            {
                switch (node)
                {
                    case ALWAYS_ANGRY_PIG_ZOMBIES:
                    case ANIMAL_EXP_NERF:
                    case ANIMAL_OVERCROWD_CONTROL:
                    case BETTER_TNT:
                    case BETTER_TREE_CHOPPING:
                    case BLAZES_EXPLODE_ON_DEATH:
                    case CANT_CRAFT_MELONSEEDS:
                    case CHARGED_CREEPERS_EXPLODE_ON_HIT:
                    case DONT_MOVE_WATER_SOURCE_BLOCKS:
                    case ENDER_DRAGON_ADDITIONAL_ATTACKS:
                    case ENHANCED_ENVIRONMENTAL_DAMAGE:
                    case EXTINGUISHING_FIRE_IGNITES_PLAYERS:
                    case FORTRESS_PIGS_DROP_WART:
                        //case GHASTS_DEFLECT_ARROWS:
                    case IMPROVED_ENDERMAN_TELEPORTATION:
                    case INHIBIT_MONSTER_GRINDERS:
                    case LIMITED_BLOCK_PLACEMENT:
                    case LIMITED_TORCH_PLACEMENT:
                    case MAGMA_CUBES_BECOME_BLAZES_ON_DAMAGE:
                        //case MORE_MONSTERS_MULTIPLIER:
                        //case NETHER_PIGS_DROP_WART:
                    case NO_BONEMEAL_ON_MUSHROOMS:
                    case NO_FARMING_NETHER_WART:
                    case NO_SWIMMING_IN_ARMOR:
                    case RAIN_BREAKS_TORCHES:
                        //case SILVERFISH_CANT_ENTER_BLOCKS:
                    case SNOW_BREAKS_CROPS:
                    case SUPER_HARD_STONE:
                    case SUPER_HARD_STONE_PHYSICS:
                    case SPIDERS_DROP_WEB_ON_DEATH:
                    case SQUID_ONLY_SPAWN_IN_OCEAN:
                    case WEAK_FOOD_CROPS:
                    case WITCHES_ADDITIONAL_ATTACKS:
                    case ZOMBIES_DEBILITATE_PLAYERS:
                    {
                        Metrics.Graph graph1 = metrics.createGraph(getLastPart(node));
                        final int metricsVal = getMetricsValue(node);
                        graph1.addPlotter(
                                new Metrics.Plotter("Completely disabled")
                                {
                                    @Override
                                    public int getValue()
                                    {
                                        return metricsVal == 0 ? 1 : 0;
                                    }

                                });
                        graph1.addPlotter(
                                new Metrics.Plotter("Enabled in all worlds")
                                {
                                    @Override
                                    public int getValue()
                                    {
                                        return metricsVal == 1 ? 1 : 0;
                                    }

                                });
                        graph1.addPlotter(
                                new Metrics.Plotter("Enabled in some")
                                {
                                    @Override
                                    public int getValue()
                                    {
                                        return metricsVal == 2 ? 1 : 0;
                                    }

                                });
                        break;
                    }
                }
            }

            metrics.start();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static String getLastPart(ConfigNode node)
    {
        String path = node.getPath();
        String[] split = path.split("\\."); //Durr it's a regex...
        return split.length > 0 ? split[split.length - 1] : "";
    }


    /**
     * Get a value to be used by metrics
     * <pre>
     * Boolean values:
     * 0 = completely disabled
     * 1 = enabled in all worlds
     * 2 = Enabled in some worlds
     *
     * Integers:
     * -----------
     * Percentages:
     * 0 = 0%
     * 1 = 1-20%
     * 2 = 21-40%
     * 3 = 41-60%
     * 4 = 61-80%
     * 5 = 81-100%
     * Health:
     * 0 = 0
     * 1 = 1-5
     * 2 = 6-10
     * 3 = 11-15
     * 4 = 16-19
     * 5 = 20
     * Y-Value:
     * 0 = 0
     * 1 = 1-50
     * 2 = 51-100
     * 3 = 101-150
     * 4 = 151-200
     * 5 = 201-255
     * </pre>
     *
     * @param node to get the value for
     */
    public int getMetricsValue(ConfigNode node)
    {
        switch (node.getVarType())
        {
            case BOOLEAN:
            {
                //Add up how often it's enabled
                int value = 0;
                for (String world : CFG.getEnabledWorlds())
                    value += CFG.getBoolean(node, world) ? 1 : 0;
                return value == 0 ? 0 : value == CFG.getEnabledWorlds().length ? 1 : 2;
            }
            default:
                throw new UnsupportedOperationException(node.getPath() + " " + node.getVarType().name() + " not supported yet!");
        }
    }
}
