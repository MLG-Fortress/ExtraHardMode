package com.extrahardmode.service;


import java.util.Random;

/**
 * @author Diemex
 */
public class OurRandom
{
    private static Random rdm = new Random();


    public static double nextDouble()
    {
        return rdm.nextDouble();
    }


    public static int nextInt(int range)
    {
        return rdm.nextInt(range);
    }


    public static boolean percentChance(int percentage)
    {
        return nextInt(100) < percentage;
    }


    /**
     * Get a random index given the weights
     *
     * @param itemWeights list of weights as integers
     *
     * @return the index of the items chosen
     */
    public static int weightedRandom(Integer[] itemWeights)
    {
        // Compute the total weight of all items together
        int totalWeight = 0;
        for (int i : itemWeights)
            totalWeight += i;
        // Now choose a random item
        int randomIndex = -1;
        double random = Math.random() * totalWeight;
        for (int i = 0; i < itemWeights.length; ++i)
        {
            random -= itemWeights[i];
            if (random <= 0.0d)
            {
                randomIndex = i;
                break;
            }
        }
        return randomIndex;
    }


    /**
     * Create a new Random object, call when plugin is enabled to ensure it's fresh etc.
     */
    public static void reload()
    {
        rdm = new Random();
    }
}
