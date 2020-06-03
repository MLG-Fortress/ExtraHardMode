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

// @RunWith(PowerMockRunner.class)
// @PrepareForTest({MultiWorldConfig.class, JavaPlugin.class, PluginLogger.class})
public class TestMultiWorldConfig
{

    // TODO: test

//    private final ExtraHardMode plugin = new MockExtraHardMode().get();
//
//
//    //Because MultiworldConfig has a constructor and is an interface
//    private class Mock extends MultiWorldConfig
//    {
//        public Mock(ExtraHardMode plugin)
//        {
//            super(plugin);
//        }
//
//
//        @Override
//        public void load()
//        {
//        }
//
//
//        @Override
//        public void starting()
//        {
//        }
//
//
//        @Override
//        public void closing()
//        {
//        }
//    }
//
//
//    /**
//     * Our Config<pre>
//     * BOOL_TRUE = false
//     * BOOL_FALSE = false
//     * INT_0 = 4
//     * INT_9 = 9
//     * STR_0 = inherit
//     * <p/>
//     * NOTFOUND_X = all not set in the config
//     * INHERITS_X = all "inherit"
//     * </pre>
//     */
//    private final FileConfiguration config = new YamlConfiguration();
//
//
//    @Before
//    public void prepare()
//    {
//        //normal values
//        config.set(MockConfigNode.BOOL_TRUE.getPath(), false);
//        config.set(MockConfigNode.BOOL_FALSE.getPath(), false);
//        config.set(MockConfigNode.INT_0.getPath(), 4);
//        config.set(MockConfigNode.INT_9.getPath(), 9);
//        config.set(MockConfigNode.STR_0.getPath(), Mode.INHERIT.name());
//
//        //inherited values
//        config.set(MockConfigNode.INHERITS_BOOL.getPath(), Mode.INHERIT.name());
//        config.set(MockConfigNode.INHERITS_INT.getPath(), Mode.INHERIT.name());
//        config.set(MockConfigNode.INHERITS_DOUBLE.getPath(), Mode.INHERIT.name().toLowerCase());//just to test if that also works
//        config.set(MockConfigNode.INHERITS_STR.getPath(), Mode.INHERIT.name());
//        config.set(MockConfigNode.INHERITS_LIST.getPath(), Mode.INHERIT.name().toLowerCase());
//    }
//
//
//    private final MultiWorldConfig module = new Mock(plugin);
//
//
//    /**
//     * Test if normal retrieval of nodes which are present int the config is possible
//     */
//    @Test
//    public void testLoadNodeValidInput()
//    {
//        assertEquals(false, module.loadNode(config, MockConfigNode.BOOL_TRUE, false).getContent());
//        assertTrue(module.loadNode(config, MockConfigNode.BOOL_TRUE, false).getStatusCode() == Status.OK);
//
//        assertEquals(false, module.loadNode(config, MockConfigNode.BOOL_FALSE, false).getContent());
//        assertTrue(module.loadNode(config, MockConfigNode.BOOL_FALSE, false).getStatusCode() == Status.OK);
//
//        assertEquals(4, module.loadNode(config, MockConfigNode.INT_0, false).getContent());
//        assertTrue(module.loadNode(config, MockConfigNode.INT_0, false).getStatusCode() == Status.OK);
//
//        assertEquals(9, module.loadNode(config, MockConfigNode.INT_9, false).getContent());
//        assertTrue(module.loadNode(config, MockConfigNode.INT_0, false).getStatusCode() == Status.OK);
//
//        assertEquals(Mode.INHERIT.name(), (String) module.loadNode(config, MockConfigNode.STR_0, false).getContent());
//        assertTrue(module.loadNode(config, MockConfigNode.STR_0, false).getStatusCode() == Status.INHERITS);
//    }
//
//
//    /**
//     * Make sure that when a node is not found in the config that we get the default value back (not null). and that the
//     * Status is NOT_FOUND
//     */
//    @Test
//    public void testLoadNodeNotFound()
//    {
//        assertEquals(MockConfigNode.NOTFOUND_BOOL.getDefaultValue(), module.loadNode(config, MockConfigNode.NOTFOUND_BOOL, false).getContent());
//        assertTrue(module.loadNode(config, MockConfigNode.NOTFOUND_BOOL, false).getStatusCode() == Status.NOT_FOUND);
//
//        assertEquals(MockConfigNode.NOTFOUND_DOUBLE.getDefaultValue(), module.loadNode(config, MockConfigNode.NOTFOUND_DOUBLE, false).getContent());
//        assertTrue(module.loadNode(config, MockConfigNode.NOTFOUND_DOUBLE, false).getStatusCode() == Status.NOT_FOUND);
//
//        assertEquals(MockConfigNode.NOTFOUND_INT.getDefaultValue(), module.loadNode(config, MockConfigNode.NOTFOUND_INT, false).getContent());
//        assertTrue(module.loadNode(config, MockConfigNode.NOTFOUND_INT, false).getStatusCode() == Status.NOT_FOUND);
//
//        assertEquals(MockConfigNode.NOTFOUND_STR.getDefaultValue(), module.loadNode(config, MockConfigNode.NOTFOUND_STR, false).getContent());
//        assertTrue(module.loadNode(config, MockConfigNode.NOTFOUND_STR, false).getStatusCode() == Status.NOT_FOUND);
//
//        assertEquals(MockConfigNode.NOTFOUND_LIST.getDefaultValue(), module.loadNode(config, MockConfigNode.NOTFOUND_LIST, false).getContent());
//        assertTrue(module.loadNode(config, MockConfigNode.NOTFOUND_LIST, false).getStatusCode() == Status.NOT_FOUND);
//
//
//        assertEquals(true, module.loadNode(config, MockConfigNode.NOTFOUND_BOOL, true).getContent());
//        assertTrue(module.loadNode(config, MockConfigNode.NOTFOUND_BOOL, true).getStatusCode() == Status.ADJUSTED);
//
//        assertEquals(1, module.loadNode(config, MockConfigNode.NOTFOUND_INT, true).getContent());
//        assertTrue(module.loadNode(config, MockConfigNode.NOTFOUND_INT, true).getStatusCode() == Status.ADJUSTED);
//
//    }
//
//
//    /**
//     * Make sure that the Status returned is Status.INHERITS for all nodes with "inherit" as value and that "inherit" is
//     * the value returned (lowercase)
//     */
//    @Test
//    public void testLoadNodeInherited()
//    {
//        String inherit = Mode.INHERIT.name().toLowerCase();
//        Response response;
//
//        response = module.loadNode(config, MockConfigNode.INHERITS_BOOL, false);
//        assertEquals(inherit, response.getContent());
//        assertTrue(response.getStatusCode() == Status.INHERITS);
//
//        response = module.loadNode(config, MockConfigNode.INHERITS_INT, false);
//        assertEquals(inherit, response.getContent());
//        assertTrue(response.getStatusCode() == Status.INHERITS);
//
//        response = module.loadNode(config, MockConfigNode.INHERITS_DOUBLE, false);
//        assertEquals(inherit, response.getContent());
//        assertTrue(response.getStatusCode() == Status.INHERITS);
//
//        response = module.loadNode(config, MockConfigNode.INHERITS_STR, false);
//        assertEquals(Mode.INHERIT.name(), response.getContent()); //only for strings the expected output is the same as the input
//        assertTrue(response.getStatusCode() == Status.INHERITS);
//
//        response = module.loadNode(config, MockConfigNode.INHERITS_LIST, false);
//        assertEquals(inherit, response.getContent());
//        assertTrue(response.getStatusCode() == Status.INHERITS);
//    }
//
//
//    /**
//     * Throw everything at the method
//     */
//    @Test
//    public void testValPercent()
//    {
//        Response response;
//
//        response = module.validateInt(MockConfigNode.INT_PERC_1, -123);
//        assertEquals(0, response.getContent());
//        assertTrue(response.getStatusCode() == Status.ADJUSTED);
//
//        response = module.validateInt(MockConfigNode.INT_PERC_1, 42);
//        assertEquals(42, response.getContent());
//        assertTrue(response.getStatusCode() == Status.OK);
//
//        response = module.validateInt(MockConfigNode.INT_PERC_1, 0);
//        assertEquals(0, response.getContent());
//        assertTrue(response.getStatusCode() == Status.OK);
//
//        response = module.validateInt(MockConfigNode.INT_PERC_1, 100);
//        assertEquals(100, response.getContent());
//        assertTrue(response.getStatusCode() == Status.OK);
//
//        response = module.validateInt(MockConfigNode.INT_PERC_1, 101);
//        assertEquals(100, response.getContent());
//        assertTrue(response.getStatusCode() == Status.ADJUSTED);
//
//        response = module.validateInt(MockConfigNode.INT_PERC_1, 1032);
//        assertEquals(100, response.getContent());
//        assertTrue(response.getStatusCode() == Status.ADJUSTED);
//    }
//
//
//    /**
//     * Test the customBounds method through the health subtype Allowed values are 1-20
//     */
//    @Test
//    public void testCustomBounds()
//    {
//        Response response;
//
//        response = module.validateInt(MockConfigNode.INT_HP_1, -123);
//        assertEquals(1, response.getContent());
//        assertTrue(response.getStatusCode() == Status.ADJUSTED);
//
//        response = module.validateInt(MockConfigNode.INT_HP_1, 0);
//        assertEquals(1, response.getContent());
//        assertTrue(response.getStatusCode() == Status.ADJUSTED);
//
//        response = module.validateInt(MockConfigNode.INT_HP_1, 23);
//        assertEquals(20, response.getContent());
//        assertTrue(response.getStatusCode() == Status.ADJUSTED);
//
//        response = module.validateInt(MockConfigNode.INT_HP_1, 12);
//        assertEquals(12, response.getContent());
//        assertTrue(response.getStatusCode() == Status.OK);
//    }
//
//
//    /**
//     * Valid numbers are only positive including 0
//     */
//    @Test
//    public void testValNaturalNumbers()
//    {
//        Response response;
//
//        response = module.validateInt(MockConfigNode.INT_NN_1, -123);
//        assertEquals(0, response.getContent());
//        assertTrue(response.getStatusCode() == Status.ADJUSTED);
//
//        response = module.validateInt(MockConfigNode.INT_NN_1, -1);
//        assertEquals(0, response.getContent());
//        assertTrue(response.getStatusCode() == Status.ADJUSTED);
//
//        response = module.validateInt(MockConfigNode.INT_NN_1, 123);
//        assertEquals(123, response.getContent());
//        assertTrue(response.getStatusCode() == Status.OK);
//
//        response = module.validateInt(MockConfigNode.INT_NN_1, 42);
//        assertEquals(42, response.getContent());
//        assertTrue(response.getStatusCode() == Status.OK);
//
//        response = module.validateInt(MockConfigNode.INT_NN_1, 1);
//        assertEquals(1, response.getContent());
//        assertTrue(response.getStatusCode() == Status.OK);
//    }
}