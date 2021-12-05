package de.diemex.scoreboardnotifier.message;


import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Methods for splitting strings into smaller chunks in an intelligent way
 */
public class StringUtil
{
    /**
     * Wrap words in an intelligent way to display in a scoreboard
     *
     * @param message to wrap
     *
     * @return the wrapped lines
     */
    public static List<MsgLineHolder> getLines(String message)
    {
        return getLines(message, null, "", "");
    }


    /**
     * Wrap words in an intelligent way to display in a scoreboard
     *
     * @param message to wrap
     *
     * @return the wrapped lines
     */
    public static List<MsgLineHolder> getLines(String message, ChatColor color)
    {
        return getLines(message, color, "", "");
    }


    /**
     * Wrap words in an intelligent way to display in a scoreboard
     *
     * @param message   to wrap
     * @param lineColor colors of the lines, reduces the line size by 2
     *
     * @return the wrapped lines
     */
    public static List<MsgLineHolder> getLines(String message, ChatColor lineColor, String linePrefix, String suffix)
    {
        Validate.notNull(message, "Null string");

        if (linePrefix == null)
            linePrefix = "";
        if (suffix == null)
            suffix = "";

        List<MsgLineHolder> lines = new ArrayList<MsgLineHolder>();

        String[] words;
        if (message.contains(" "))
            words = message.split(" ");
        else
            words = new String[]{message};

        MsgLineHolder line = new MsgLineHolder();
        int offset = 0;
        int maxLineLength = lineColor != null ? 38 : 40; //Reserve 2 chars for the color code
        //Append every word to a line
        for (int i = 0; i < words.length; i++)
        {
            //Fresh line
            if (line.length() == 0)
            {
                //Set properties
                if (lineColor != null) //need color code for every line
                    line.setLineColor(lineColor);
                line.setPrefix(linePrefix);
                //If word exceeds the maximum line length cut it
                if (words[i].length() - offset > maxLineLength)
                {
                    lines.add(line.append(words[i].substring(offset, maxLineLength - 1 + offset) + "-"));
                    offset += maxLineLength - 1;
                    i--;
                }
                //If word fits just append it
                else
                {
                    line.append(words[i].substring(offset));
                    offset = 0;
                }
            }
            //Line already has some words
            else
            {
                //If appending the current word would make line exceed the maximum length go to next line
                if (words[i].length() + line.length() > maxLineLength - 1) //account for the space between words
                {
                    //If there is only 2 empty chars available or the word that would have been cut is shorter than two chars, rather create a new line
                    if (line.length() >= maxLineLength - 2 || line.length() + words[i].length() < maxLineLength + 2 && words[i].length() < 5 && line.length() > maxLineLength - 2)
                    {
                        lines.add(line);
                        line = new MsgLineHolder();
                        i--;
                    }
                    //Cut and append the word to the current line
                    else
                    {
                        offset = maxLineLength - 2 - line.length();
                        line.append(" " + words[i].substring(0, maxLineLength - 2 - line.length()) + "-");
                        lines.add(line);
                        line = new MsgLineHolder();
                        i--;
                    }
                }
                //Normal appending of word, no exceeding of line
                else
                {
                    offset = 0;
                    line.append(" " + words[i]);
                }
            }
        }
        //Append suffix to last MsgLineHolder
        if (suffix.length() > 0)
        {
            if (line.length() + suffix.length() < maxLineLength)
                line.setSuffix(suffix);
            else
            {
                lines.add(line);
                line = new MsgLineHolder().setSuffix(suffix);
            }
        }
        lines.add(line);
        return lines;
    }


    /**
     * Test of a list contains one or more equal elements
     *
     * @param first  first list
     * @param second second list to compare
     *
     * @return true if one or more elements are the same, false if lists are completely different
     */
    public static boolean containsOneEqualElem(List first, List second)
    {
        for (Object str : first)
        {
            if (second.contains(str))
                return true;
        }
        return false;
    }
}
