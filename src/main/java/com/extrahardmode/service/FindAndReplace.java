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

package com.extrahardmode.service;


/**
 * Holds information about a FindAndReplace
 * <p/>
 * first is the variable/placeholder to be replaced second is the value to replace the variable (placeholder) with
 *
 * @author Max
 */
public class FindAndReplace
{
    private final String replaceWith;

    private final String[] searchFor;


    /**
     * Constructor
     *
     * @param searchFor   a String
     * @param replaceWith some other String
     */
    public FindAndReplace(String replaceWith, String... searchFor)
    {
        this.searchFor = searchFor;
        this.replaceWith = replaceWith;
    }


    /**
     * What do we want to replace it with
     *
     * @return the replacer-String
     */
    public String getReplaceWith()
    {
        return replaceWith;
    }


    /**
     * What are we looking for
     *
     * @return the search-String
     */
    public String[] getSearchWord()
    {
        return searchFor.clone();
    }


    /**
     * Run the ReplaceOperation on the given String
     *
     * @param input string to search in
     * @return the input string with the replaced strings
     */
    public String replace(String input)
    {
        for (String search : searchFor)
        {
            input = input.replace(search, replaceWith);
        }
        return input;
    }

}
