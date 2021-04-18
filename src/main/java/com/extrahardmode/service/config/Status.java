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
 * Easier to read than meaningless null return values
 */
public enum Status
{
    /**
     * The config has been adjusted and needs to be saved
     */
    ADJUSTED,
    /**
     * This value should be rewritten back to config, but hasn't been adjusted yet, or needs to be adjusted by another method
     */
    NEEDS_TO_BE_ADJUSTED,
    /**
     * Config hasn't been altered and doesn't need to be saved
     */
    OK,
    /**
     * Requested value not found
     */
    NOT_FOUND,
    /**
     * For use as a default for another Object (e.g. Config)
     */
    DEFAULTS,
    /**
     * This value inherits from something
     */
    INHERITS,
    /**
     * The values to disable this option should be loaded
     */
    DISABLES,
    /**
     * This Object has been fully processed and will be ignored
     */
    PROCESSED
}
