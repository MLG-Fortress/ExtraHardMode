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

package com.extrahardmode.service.config;


import org.bukkit.ChatColor;

/**
 * Parses a given StringList into a Map containing the block ids and metadatavalues if existing The supported input can be formatted like this
 * <p/>
 * 13:1,2,3 | COBBLESTONE.2.3.4 | 13:3.4.5
 * <p/>
 * The first number/string is always expected to be the string everything after that can be seperated by any symbol which isn't a number/character
 * <p/>
 * So 13:1@4%3 -> [13]{1,3,4} 13 is the blockid and 1,3,4 is the blockmetadatavalues which are allowed
 *
 * @author Max
 */
public class SpecialParsers
{

    /**
     * Match a ChatColor
     *
     * @param input input string to match
     * @return ChatColor or null if not a valid ChatColor
     */
    public static ChatColor parseColor(String input)
    {
        ChatColor color = null;
        if (input == null)
            return null;
        try
        {
            color = ChatColor.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException ignored)
        {
            //color = ChatColor.getByChar(input.replaceAll("[^0-9a-f]", "").substring(0, 1)); //TODO test
        }
        return color;

    }


    /**
     * Parse a given List of Strings which represent Blocks and their Metadata
     *
     * @param stringList of representing blocks
     *
     * @return a Map which is usable by a plugin and the StatusCode. <br> OK = the input has been completely valid, <br>
     * NEEDS_TO_BE_ADJUSTED = input not valid but has been corrected and can be written back to config
     */
//    public static Response<Map<Integer/*block id*/, List<Byte>>> parseMaterials(List<String> stringList)
//    {
//        Status status = Status.OK;
//
//        Map<Integer, List<Byte>> myFallingBlocks = new HashMap<Integer, List<Byte>>();
//
//        for (String blockString : stringList)
//        {
//            /* Use Pattern because it's 25% faster than String.replaceAll() */
////            Pattern whitespace = Pattern.compile("\\s"); //Includes tabs/newline characters
////            if (whitespace.matcher(blockString).find())
////            {
////                blockString = whitespace.matcher(blockString).replaceAll("");
////                status = Status.NEEDS_TO_BE_ADJUSTED;
////            }
//
//            List<Byte> meta = new ArrayList<Byte>();
//
//            /* String will be in this format 34:4 / ANVIL / LOG:1,2,3 */
//            /* any non letters/digits/underscores means there is meta*/
//            Pattern seperators = Pattern.compile("[^A-Za-z0-9_]");
//            Matcher mSeperators = seperators.matcher(blockString);
//
//            Pattern onlyNumbers = Pattern.compile("[^0-9]");
//
//            if (mSeperators.find())
//            {
//                /* So we know if we should rewrite the config */
//                Pattern invalidSeperators = Pattern.compile("[^A-Za-z0-9_@,]");
//                Matcher mInvalidSeperators = invalidSeperators.matcher(blockString);
//                if (mInvalidSeperators.find())
//                {
//                    status = Status.NEEDS_TO_BE_ADJUSTED;
//                }
//
////                String[] splitted = seperators.split(blockString);
////
////                for (int i = 1; i < splitted.length; i++)
////                {
//                    /* Meta can only be numbers */
////                    splitted[i] = onlyNumbers.matcher(splitted[i]).replaceAll("");
////
////                    if (!splitted[i].isEmpty())
////                    {
////                        /* Prevent out of range errors */
////                        int metaInt = Integer.parseInt(splitted[i]);
////                        if (metaInt < Byte.MIN_VALUE)
////                        {
////                            metaInt = Byte.MIN_VALUE;
////                            status = Status.NEEDS_TO_BE_ADJUSTED;
////                        } else if (metaInt > Byte.MAX_VALUE)
////                        {
////                            metaInt = Byte.MAX_VALUE;
////                            status = Status.NEEDS_TO_BE_ADJUSTED;
////                        }
////                        meta.add((byte) metaInt);
////                    }
////                }
//            }
//
//            String blockId = seperators.split(blockString)[0];
//            Material material = Material.matchMaterial(blockId);
//
//            /* couldn't be matched by enum constant */
//            if (material == null)
//            {
//                /* try as number (blockId) */
//                String tempId = onlyNumbers.matcher(blockId).replaceAll("");
//                if (!tempId.isEmpty())
//                {
//                    material = Material.getMaterial(tempId);
//                }
//                /* still fail -> try as enum again but strip numbers */
//                if (material == null)
//                {
//                    Pattern onlyLetters = Pattern.compile("[^a-zA-Z_]");
//                    material = Material.matchMaterial(onlyLetters.matcher(blockId).replaceAll(""));
//                }
//                /* we identified the block, but we should save it correctly so we don't have to do it again */
//                if (material != null)
//                {
//                    status = Status.NEEDS_TO_BE_ADJUSTED;
//                }
//            }
//
//            String onlyNums = onlyNumbers.matcher(blockId).replaceAll("").length() > 0
//                    ? onlyNumbers.matcher(blockId).replaceAll("")
//                    : "0";
//            int blockNumber = material != null ? material.getId() : Integer.parseInt(onlyNums);
//
//            /* merge data if the block is in here already */
//            if (myFallingBlocks.containsKey(blockNumber))
//            {
//                List<Byte> oldMeta = myFallingBlocks.get(blockNumber);
//                meta.addAll(oldMeta);
//                Collections.sort(meta);
//                status = Status.NEEDS_TO_BE_ADJUSTED;
//            }
//
//            if (blockNumber != 0) //AIR can't fall and 0 will be default if parsing failed
//                myFallingBlocks.put(blockNumber, meta);
//
//        }
//
//        return new Response<Map<Integer, List<Byte>>>(status, myFallingBlocks);
//    }


    /**
     * Turn a given Representation of Blocks and their metaData into a human readable form
     *
     * @return List to be written to config
     */
//    public static List<String> convertToStringList(Map<Integer, List<Byte>> blocksWithMeta)
//    {
//        List<String> blockList = new ArrayList<String>(blocksWithMeta.size());
//
//        for (Map.Entry<Integer, List<Byte>> metaBlock : blocksWithMeta.entrySet())
//        {
//            StringBuilder builder = new StringBuilder();
//            Material material = Material.getMaterial(metaBlock.getKey());
//            builder.append(material != null ? material.name() : metaBlock.getKey());
//
//            List<Byte> metaData = metaBlock.getValue();
//            metaData = metaData == null ? Collections.<Byte>emptyList() : metaData;
//            for (int i = 0; i < metaData.size(); i++)
//            {
//                if (i == 0)
//                    builder.append('@');
//                else
//                    builder.append(',');
//                builder.append(metaData.get(i));
//            }
//            blockList.add(builder.toString());
//        }
//
//        return blockList;
//    }
}
