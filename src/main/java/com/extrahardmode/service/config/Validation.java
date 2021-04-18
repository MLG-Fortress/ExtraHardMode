package com.extrahardmode.service.config;


//             _ _     _       _   _
// /\   /\__ _| (_) __| | __ _| |_(_) ___  _ __
// \ \ / / _` | | |/ _` |/ _` | __| |/ _ \| '_ \
//  \ V / (_| | | | (_| | (_| | |_| | (_) | | | |
//   \_/ \__,_|_|_|\__,_|\__,_|\__|_|\___/|_| |_|
public class Validation
{
    /**
     * Verify that the ConfigNode contains a valid value and is usable by the Plugin
     *
     * @param node  the ConfigNode to validate, validates according to the SubType of the ConfigNode
     * @param value the current value to validate
     * @return validated value, if value null then defaultValue is returned
     */
    public static Integer validateInt(final ConfigNode node, Object value)
    {
        Integer validated = 0;
        if (node.getVarType() == (ConfigNode.VarType.INTEGER))
        {
            if (value instanceof Integer)
            {
                int valMe = (Integer) value;

                if (node.getSubType() != null)
                {
                    switch (node.getSubType())
                    {
                        case PERCENTAGE:
                        {
                            validated = validatePercentage(node, valMe);
                            break;
                        }
                        case Y_VALUE:
                        {
                            validated = validateYCoordinate(node, valMe);
                            break;
                        }
                        case HEALTH:
                        {
                            validated = validateCustomBounds(node, 1, 20, valMe);
                            break;
                        }
                        case NATURAL_NUMBER:
                        {
                            validated = validateCustomBounds(node, 0, 0, valMe);
                            break;
                        }
                        default:
                            throw new UnsupportedOperationException("SubType of " + node.getPath() + " doesn't have a validation method");
                    }
                }
            } else
            {
                return (Integer) node.getDefaultValue();
            }
        } else
        {
            throwWrongTypeError(node, ConfigNode.VarType.INTEGER);
        }
        return validated;
    }


    /**
     * Validate Y coordinate limit for the given configuration option against the list of enabled worlds.
     *
     * @param node  - Root node to validate.
     * @param value - Integer to validate
     * @return validated value
     */
    public static Integer validateYCoordinate(ConfigNode node, Integer value)
    {
        if (node.getVarType() == ConfigNode.VarType.INTEGER)
        {
            if (value == null)
                value = (Integer) node.getDefaultValue();
            int maxHeight = 255;
            if (value < 0)
                value = 0;
            else if (value > maxHeight)
                value = maxHeight;
        } else
            throwWrongTypeError(node, ConfigNode.VarType.INTEGER);
        return value;
    }


    /**
     * Validate percentage (0-100) value for given configuration option.
     *
     * @param node  - Root node to validate.
     * @param value - Integer to validate
     * @return validated value
     */
    public static Integer validatePercentage(ConfigNode node, Integer value)
    {
        if (node.getVarType() == ConfigNode.VarType.INTEGER)
        {
            if (value < 0)
                value = 0;
            else if (value > 100)
                value = 100;
        } else throwWrongTypeError(node, ConfigNode.VarType.INTEGER);
        return value;
    }


    /**
     * Validates a configOption with custom bounds
     *
     * @param node   the configNode
     * @param minVal the minimum value the config is allowed to have
     * @param maxVal the maximum value for the config, if == minVal then it doesn't get checked
     * @param value  - Integer to validate
     * @return validated value
     */
    public static Integer validateCustomBounds(ConfigNode node, int minVal, int maxVal, Integer value)
    {
        if (node.getVarType() == ConfigNode.VarType.INTEGER)
        {
            if (value < minVal)
            {
                value = minVal;
            } else if (minVal < maxVal && value > maxVal)
            {
                value = maxVal;
            }
        } else throwWrongTypeError(node, ConfigNode.VarType.INTEGER);
        return value;
    }


    private static void throwWrongTypeError(ConfigNode node, ConfigNode.VarType type)
    {
        throw new IllegalArgumentException("Expected a ConfigNode with Type " + type.name() + " but got " + node.getVarType().name() + " for " + node.getPath());
    }
}
