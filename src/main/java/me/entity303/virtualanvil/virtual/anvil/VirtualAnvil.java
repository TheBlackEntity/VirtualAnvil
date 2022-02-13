package me.entity303.virtualanvil.virtual.anvil;

import me.entity303.virtualanvil.virtual.containeraccess.ContainerAccessWrapper;
import me.entity303.virtualanvil.virtual.containeraccess.ContainerAccess_Latest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class VirtualAnvil {
    protected static Method getInventoryMethod = null;
    protected static Method sendPacketMethod = null;
    protected static Method initMenuMethod = null;
    protected static Method getBukkitViewMethod = null;
    protected static Field containerField = null;
    private static String version = "v1_18_R1";

    private static me.entity303.virtualanvil.virtual.anvil.VirtualAnvil virtualAnvil = null;

    public static void load() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            version = "v1_18_R1";
        }
        if (version.contains("1_8") || version.contains("1_9") || version.contains("1_10") || version.contains("1_11") || version.contains("1_12") || version.contains("1_13"))
            virtualAnvil = new VirtualAnvil_v1_8_R3_To_v1_13_R2();
        else try {
            Class.forName("net.minecraft.network.protocol.Packet");
            virtualAnvil = new VirtualAnvil_Latest();
        } catch (ClassNotFoundException | NoClassDefFoundError ignored) {
            virtualAnvil = new VirtualAnvil_v1_14_R1_To_v1_16_R3();
        }
        VirtualAnvil.version = version;
    }

    public static String getVersion() {
        return version;
    }

    public static me.entity303.virtualanvil.virtual.anvil.VirtualAnvil getVirtualAnvil() {
        return virtualAnvil;
    }

    public static ContainerAccessWrapper getWrapper(Player player) {
        try {
            Class clazz = Class.forName(ContainerAccess_Latest.class.getPackage().getName() + ".ContainerAccess_" + getVersion());
            return (ContainerAccessWrapper) clazz.getConstructor(Player.class).newInstance(player);
        } catch (ClassNotFoundException ignored) {
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            Class clazz = ContainerAccess_Latest.class;
            return (ContainerAccessWrapper) clazz.getConstructor(Player.class).newInstance(player);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract void openAnvil(Player player);
}
