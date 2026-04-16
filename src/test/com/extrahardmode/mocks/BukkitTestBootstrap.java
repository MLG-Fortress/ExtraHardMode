package com.extrahardmode.mocks;


import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Minimal Bukkit bootstrap for tests that now touch registry-backed API.
 */
public final class BukkitTestBootstrap
{
    private BukkitTestBootstrap()
    {
    }


    public static void install()
    {
        clearServer();

        Server server = mock(Server.class);
        Logger logger = Logger.getLogger(BukkitTestBootstrap.class.getName());
        ItemFactory itemFactory = mock(ItemFactory.class);
        Map<Class<?>, Registry<?>> bootstrapRegistries = new ConcurrentHashMap<Class<?>, Registry<?>>();

        when(server.getLogger()).thenReturn(logger);
        when(server.getName()).thenReturn("TestBukkit");
        when(server.getVersion()).thenReturn("test");
        when(server.getBukkitVersion()).thenReturn("test");
        when(server.getItemFactory()).thenReturn(itemFactory);
        when(itemFactory.asMetaFor(any(ItemMeta.class), any(Material.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(itemFactory.getItemMeta(any(Material.class))).thenReturn(null);

        when(server.getRegistry(any())).thenAnswer(invocation ->
        {
            Class<?> registryClass = invocation.getArgument(0);
            return resolveBootstrapRegistry(bootstrapRegistries, registryClass);
        });

        Bukkit.setServer(server);

        Registry<?> materialRegistry = Registry.MATERIAL;
        Registry<?> entityRegistry = Registry.ENTITY_TYPE;
        Registry<?> effectRegistry = Registry.EFFECT;

        when(server.getRegistry(any())).thenAnswer(invocation ->
        {
            Class<?> registryClass = invocation.getArgument(0);
            if (Material.class.equals(registryClass))
                return materialRegistry;
            if (EntityType.class.equals(registryClass))
                return entityRegistry;
            if (PotionEffectType.class.equals(registryClass))
                return effectRegistry;
            return resolveBootstrapRegistry(bootstrapRegistries, registryClass);
        });
    }


    public static void reset()
    {
        clearServer();
    }


    @SuppressWarnings("unchecked")
    private static <T extends org.bukkit.Keyed> Registry<T> createFallbackRegistry()
    {
        return (Registry<T>) Proxy.newProxyInstance(
                BukkitTestBootstrap.class.getClassLoader(),
                new Class<?>[]{Registry.class},
                new EmptyRegistryHandler());
    }


    @SuppressWarnings("unchecked")
    private static Registry<PotionEffectType> createPotionRegistry()
    {
        return (Registry<PotionEffectType>) Proxy.newProxyInstance(
                BukkitTestBootstrap.class.getClassLoader(),
                new Class<?>[]{Registry.class},
                new PotionRegistryHandler());
    }


    private static Registry<?> resolveBootstrapRegistry(Map<Class<?>, Registry<?>> bootstrapRegistries, Class<?> registryClass)
    {
        if (registryClass == null)
            return createFallbackRegistry();

        Registry<?> registry = bootstrapRegistries.get(registryClass);
        if (registry != null)
            return registry;

        Registry<?> createdRegistry = PotionEffectType.class.equals(registryClass) ? createPotionRegistry() : createFallbackRegistry();
        Registry<?> existingRegistry = bootstrapRegistries.putIfAbsent(registryClass, createdRegistry);
        return existingRegistry != null ? existingRegistry : createdRegistry;
    }


    private static PotionEffectType getOrCreateEffect(Map<String, PotionEffectType> effects, NamespacedKey key)
    {
        return effects.computeIfAbsent(key.toString(), ignored ->
        {
            int id = effects.size() + 1;
            return new TestPotionEffectType(id, key);
        });
    }


    private static void clearServer()
    {
        try
        {
            Field serverField = Bukkit.class.getDeclaredField("server");
            serverField.setAccessible(true);
            serverField.set(null, null);
        } catch (ReflectiveOperationException e)
        {
            throw new IllegalStateException("Unable to reset Bukkit server singleton for tests", e);
        }
    }


    private static final class EmptyRegistryHandler implements InvocationHandler
    {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
        {
            String methodName = method.getName();
            if ("get".equals(methodName) || "match".equals(methodName))
                return null;
            if ("getOrThrow".equals(methodName))
                throw new IllegalArgumentException("Missing test registry entry");
            if ("stream".equals(methodName))
                return Stream.empty();
            if ("iterator".equals(methodName))
                return Stream.empty().iterator();
            if ("toString".equals(methodName))
                return "EmptyRegistryProxy";
            if ("hashCode".equals(methodName))
                return System.identityHashCode(proxy);
            if ("equals".equals(methodName))
                return proxy == args[0];
            throw new UnsupportedOperationException("Unsupported registry method: " + methodName);
        }
    }


    private static final class PotionRegistryHandler implements InvocationHandler
    {
        private final Map<String, PotionEffectType> effects = new ConcurrentHashMap<String, PotionEffectType>();


        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
        {
            String methodName = method.getName();
            if ("get".equals(methodName) || "getOrThrow".equals(methodName))
                return getOrCreateEffect(effects, (NamespacedKey) args[0]);
            if ("match".equals(methodName))
            {
                String input = (String) args[0];
                if (input == null)
                    return null;

                NamespacedKey key = NamespacedKey.fromString(input.toLowerCase(Locale.ROOT));
                if (key == null)
                    key = NamespacedKey.minecraft(input.toLowerCase(Locale.ROOT));
                return getOrCreateEffect(effects, key);
            }
            if ("stream".equals(methodName))
                return effects.values().stream();
            if ("iterator".equals(methodName))
                return effects.values().iterator();
            if ("toString".equals(methodName))
                return "PotionRegistryProxy";
            if ("hashCode".equals(methodName))
                return System.identityHashCode(proxy);
            if ("equals".equals(methodName))
                return proxy == args[0];
            throw new UnsupportedOperationException("Unsupported registry method: " + methodName);
        }
    }


    private static final class TestPotionEffectType extends PotionEffectType
    {
        private final int id;
        private final NamespacedKey key;
        private final String name;


        private TestPotionEffectType(int id, NamespacedKey key)
        {
            this.id = id;
            this.key = key;
            this.name = key.getKey().toUpperCase(Locale.ROOT);
        }


        @Override
        public PotionEffect createEffect(int duration, int amplifier)
        {
            return new PotionEffect(this, duration, amplifier);
        }


        @Override
        public boolean isInstant()
        {
            return false;
        }


        @Override
        public PotionEffectTypeCategory getCategory()
        {
            return PotionEffectTypeCategory.NEUTRAL;
        }


        @Override
        public Color getColor()
        {
            return Color.WHITE;
        }


        @Override
        public NamespacedKey getKey()
        {
            return key;
        }


        @Override
        public double getDurationModifier()
        {
            return 1.0;
        }


        @Override
        public int getId()
        {
            return id;
        }


        @Override
        public String getName()
        {
            return name;
        }


        @Override
        public String getTranslationKey()
        {
            return "effect.minecraft." + key.getKey();
        }


        @Override
        public NamespacedKey getKeyOrThrow()
        {
            return key;
        }


        @Override
        public NamespacedKey getKeyOrNull()
        {
            return key;
        }


        @Override
        public boolean isRegistered()
        {
            return true;
        }
    }
}
