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


/**
 * Determines how to load the specific ConfigFile
 */
public enum Mode
{
    /**
     * This is the main configFile and gets overriden by other Configs
     */
    MAIN,
    /**
     * Override the settings of the main config in specific worlds
     */
    INHERIT,
    /**
     * All options which aren't found default to disabled, this allows to only activate a few things and not having to disable everything else
     */
    DISABLE,
    /**
     * The mode hasn't been set yet
     */
    NOT_SET
}
