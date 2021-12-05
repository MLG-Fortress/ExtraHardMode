package de.diemex.scoreboardnotifier;


import de.diemex.scoreboardnotifier.message.MsgLineHolder;
import de.diemex.scoreboardnotifier.message.MsgSettings;
import de.diemex.scoreboardnotifier.message.StringUtil;
import org.apache.commons.lang.Validate;

import java.util.List;

/**
 * Holds information about a message to be displayed
 */
public class NotificationHolder
{
    /**
     * Holds information about the length and the titleColor
     */
    private MsgSettings type;

    /**
     * Title of the message
     */
    private String title;

    /**
     * The original msg text
     */
    String msgText;

    /**
     * The message separated into new lines
     */
    private List<MsgLineHolder> msg;

    /**
     * Prefix for every line
     */
    private String linePrefix = "";

    /**
     * MessageCount at the end of message, if message is being displayed more than once
     */
    private int messageCount = 1;


    /**
     * Construct a new NotificationHolder. Breaks message into lines and breaks words.
     *
     * @param type  type of this popup
     * @param title title of the message
     * @param msg   msg as String, will be splitted into lines
     */
    public NotificationHolder(MsgSettings type, String title, String msg)
    {
        this(type, title, StringUtil.getLines(msg, type.getTextColor()));
        this.msgText = msg;
    }


    /**
     * Construct a new NotificationHolder. Uses the given pre formatted lines.
     *
     * @param type  type of this popup
     * @param title title of the message
     * @param msg   msg as String, will be splitted into lines
     */
    public NotificationHolder(MsgSettings type, String title, List<MsgLineHolder> msg)
    {
        StringBuilder sb = new StringBuilder();
        for (MsgLineHolder line : msg)
        {
            if (sb.length() != 0)
                sb.append(" ");
            Validate.isTrue(line.length() <= 40, "Scoreboards have a max of 40 characters per line. Given line was " + line.length() + " long. Content: \"" + line + "\"");
            sb.append(line);
        }
        this.msgText = sb.toString();
        this.type = type;
        this.title = title;

        this.msg = msg;//new ArrayList<MsgLineHolder>();
        //for (MsgLineHolder lineText : msg)
        //    this.msg.add(new MsgLineHolder(lineText));
        //this.plugin = plugin;
    }


    /**
     * Get the Type of this Popup. How long to display, titlecolor
     *
     * @return the type
     */
    public MsgSettings getType()
    {
        return type;
    }


    /**
     * Get the title of the message
     *
     * @return the title of a message
     */
    public String getTitle()
    {
        return title == null ? null : type.getTitleColor() != null ? type.getTitleColor() + title : title;
    }


    /**
     * Get the lines of text
     *
     * @return the lines of text with a max of 16 chars per line
     */
    public List<String> getMsg()
    {
        return MsgLineHolder.toString(msg);
    }


    /**
     * Get the lines represented as MsgLines
     *
     * @return msg lines
     */
    public List<MsgLineHolder> getMsgLines()
    {
        return msg;
    }


    /**
     * Change the count
     *
     * @param by change count by, can also be negative to decrement
     *
     * @return this NotificationHolder for methoud chaining
     */
    public NotificationHolder modifyCount(int by)
    {
        messageCount += by;
        return this;
    }


    /**
     * Get the current count of a message
     *
     * @return how often a certain message is being shown at the current time
     */
    public int getMessageCount()
    {
        return messageCount;
    }


    /**
     * Add a prefix to every line. This is needed when a message has lines which are similar/same and we need to make them slightly different
     *
     * @param linePrefix character to add.
     *
     * @return this NotificationHolder for methoud chaining
     */
    public NotificationHolder addLinePrefix(char linePrefix)
    {
        this.linePrefix += Character.toString(linePrefix);
        return this;
    }


    /**
     * Do the messages contain equal lines (will derp up if, because lines have to be unique)
     *
     * @param other Popup to compare
     *
     * @return true if one line is the same
     */
    public boolean hasOneEqualLine(NotificationHolder other)
    {
        return StringUtil.containsOneEqualElem(this.getMsg(), other.getMsg());
    }


    /**
     * Recalculate the line breaks
     *
     * @return this NotificationHolder for method chaining
     */
    public NotificationHolder redraw()
    {
        msg = StringUtil.getLines(msgText, type.getTextColor(), linePrefix, String.format(" [%d]", messageCount));
        return this;
    }


    /**
     * Compares if the message text is the same
     *
     * @param obj to compare
     *
     * @return true if equal, otherwise false
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof NotificationHolder)
        {
            label:
            if (getMsgLines().size() == ((NotificationHolder) obj).getMsgLines().size())
            {
                for (int i = 0; i < getMsgLines().size(); i++)
                {
                    if (!getMsgLines().get(i).equals(((NotificationHolder) obj).getMsgLines().get(i)))
                        break label;
                }
                return true;
            }
        }
        return false;
    }
}