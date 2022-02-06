package me.entity303.virtualanvil.virtual.anvil;

import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerAnvil;
import net.minecraft.world.inventory.Containers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class VirtualAnvil_Latest extends VirtualAnvil {

    @Override
    public void openAnvil(Player player) {
        if (getInventoryMethod == null) {
            getInventoryMethod = Arrays.stream(EntityHuman.class.getDeclaredMethods()).
                    filter(method -> method.getReturnType().getName().equalsIgnoreCase(PlayerInventory.class.getName())).
                    filter(method -> method.getParameters().length <= 0)
                    .findFirst().orElse(null);


            if (getInventoryMethod == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find method 'getInventory' in class " + EntityHuman.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            getInventoryMethod.setAccessible(true);

        }

        if (sendPacketMethod == null) {
            sendPacketMethod = Arrays.stream(PlayerConnection.class.getMethods()).
                    filter(method -> method.getParameters().length == 1).
                    filter(method -> method.getParameters()[0].getType().getName().equalsIgnoreCase(Packet.class.getName())).
                    findFirst().orElse(null);

            if (sendPacketMethod == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find method 'sendPacket' in class " + PlayerConnection.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            sendPacketMethod.setAccessible(true);
        }

        if (containerField == null) {
            containerField = Arrays.stream(EntityHuman.class.getDeclaredFields())
                    .filter(field -> field.getType().getName().equalsIgnoreCase(Container.class.getName()))
                    .findFirst().orElse(null);

            if (containerField == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find field 'container' in class " + EntityHuman.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            containerField.setAccessible(true);
        }

        if (initMenuMethod == null) {
            initMenuMethod = Arrays.stream(EntityPlayer.class.getDeclaredMethods()).
                    filter(method -> method.getParameters().length == 1).
                    filter(method -> method.getParameters()[0].getType().getName().equalsIgnoreCase(Container.class.getName())).
                    findFirst().orElse(null);

            if (initMenuMethod == null) {
                try {
                    throw new NoSuchMethodException("Couldn't find method 'initMenu' in class " + EntityPlayer.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }

            initMenuMethod.setAccessible(true);
        }

        EntityPlayer human = null;
        try {
            human = (EntityPlayer) Class.forName("org.bukkit.craftbukkit." + getVersion() + ".entity.CraftPlayer").getDeclaredMethod("getHandle").invoke(player);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }


        int id = human.nextContainerCounter();
        ContainerAnvil container = null;
        try {
            container = new ContainerAnvil(id, (PlayerInventory) getInventoryMethod.invoke(human), (ContainerAccess) getWrapper(player));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        container.setTitle(IChatBaseComponent.ChatSerializer.a("{\"text\":\"Repairing\"}"));

        container.checkReachable = false;


        try {
            sendPacketMethod.invoke(human.b, new PacketPlayOutOpenWindow(id, Containers.h, IChatBaseComponent.ChatSerializer.a("{\"text\":\"Repairing\"}")));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        try {
            containerField.set(human, container);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        try {
            initMenuMethod.invoke(human, container);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if (getBukkitViewMethod == null) {
            try {
                getBukkitViewMethod = Container.class.getDeclaredMethod("getBukkitView");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                return;
            }
            getBukkitViewMethod.setAccessible(true);
        }

        try {
            player.openInventory(((InventoryView) getBukkitViewMethod.invoke(container)));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
