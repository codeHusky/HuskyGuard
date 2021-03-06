/*
 * WorldGuard, a suite of tools for Minecraft
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldGuard team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.codehusky.huskyguard;

import com.codehusky.huskyguard.event.player.ProcessPlayerEvent;
import com.codehusky.huskyguard.listener.*;
import com.codehusky.huskyguard.session.SpongeSessionManager;
import com.codehusky.huskyguard.util.Events;
import com.codehusky.huskyguard.util.logging.ClassSourceValidator;
import com.google.common.collect.ImmutableList;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import com.sk89q.wepif.PermissionsResolverManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitCommandSender;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.sponge.SpongeCommandSender;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.registry.SimpleFlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldedit.sponge.SpongeHuskyGuardPlatform;
import com.sk89q.worldguard.util.logging.RecordMessagePrefixer;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import javax.inject.Inject;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * The main class for WorldGuard as a Bukkit plugin.
 */
@Plugin(name="HuskyGuard",id="huskyguard",version="1.0.0",description = "A WorldGuard port, made with care.")
public class HuskyGuardPlugin {

    @Inject
    public PluginContainer container;

    private static HuskyGuardPlugin inst;
    private static SpongeHuskyGuardPlatform platform;
    //private final CommandsManager<Actor> commands;
    private PlayerMoveListener playerMoveListener;

    /**
     * Construct objects. Actual loading occurs when the plugin is enabled, so
     * this merely instantiates the objects.
     */
    public HuskyGuardPlugin() {
        inst = this;
        /*commands = new CommandsManager<Actor>() {
            @Override
            public boolean hasPermission(Actor player, String perm) {
                return player.hasPermission(perm);
            }
        };*/
    }

    /**
     * Get the current instance of WorldGuard
     * @return WorldGuardPlugin instance
     */
    public static HuskyGuardPlugin inst() {
        return inst;
    }

    /**
     * Called on plugin enable.
     */
    @Listener
    public void onEnable(GameStartingServerEvent event) {
        configureLogger();



        //PermissionsResolverManager.initialize(this);

        WorldGuard.getInstance().setPlatform(platform = new SpongeHuskyGuardPlatform()); // Initialise WorldGuard
        WorldGuard.getInstance().setup();
        SpongeSessionManager sessionManager = (SpongeSessionManager) platform.getSessionManager();

        // Set the proper command injector
        //commands.setInjector(new SimpleInjector(WorldGuard.getInstance()));

        // Catch bad things being done by naughty plugins that include
        // WorldGuard's classes
        ClassSourceValidator verifier = new ClassSourceValidator(this);
        verifier.reportMismatches(ImmutableList.of(ProtectedRegion.class, ProtectedCuboidRegion.class, Flag.class));

        // Register command classes
        //final CommandsManagerRegistration reg = new CommandsManagerRegistration(this, commands);
        //reg.register(ToggleCommands.class);
        //reg.register(ProtectionCommands.class);



        Sponge.getScheduler().createTaskBuilder()
            .execute(sessionManager).delayTicks(SpongeSessionManager.RUN_DELAY).intervalTicks(SpongeSessionManager.RUN_DELAY).submit(this);

        // Register events
        Sponge.getEventManager().registerListeners(this, sessionManager);
        //TODO: change these to register listeners here, not within class
        (new HuskyGuardPlayerListener(this)).registerEvents();
        (new HuskyGuardBlockListener(this)).registerEvents();
        (new HuskyGuardEntityListener(this)).registerEvents();
        (new HuskyGuardWeatherListener(this)).registerEvents();
        (new HuskyGuardVehicleListener(this)).registerEvents();
        (new HuskyGuardServerListener(this)).registerEvents();
        (new HuskyGuardHangingListener(this)).registerEvents();

        // Modules
        (playerMoveListener = new PlayerMoveListener(this)).registerEvents();
        (new BlacklistListener(this)).registerEvents();
        (new ChestProtectionListener(this)).registerEvents();
        (new RegionProtectionListener(this)).registerEvents();
        (new RegionFlagsListener(this)).registerEvents();
        (new WorldRulesListener(this)).registerEvents();
        (new BlockedPotionsListener(this)).registerEvents();
        (new EventAbstractionListener(this)).registerEvents();
        (new PlayerModesListener(this)).registerEvents();
        (new BuildPermissionListener(this)).registerEvents();
        (new InvincibilityListener(this)).registerEvents();
        if ("true".equalsIgnoreCase(System.getProperty("worldguard.debug.listener"))) {
            (new DebuggingListener(this, WorldGuard.logger)).registerEvents();
        }



        // handle worlds separately to initialize already loaded worlds
        HuskyGuardWorldListener worldListener = (new HuskyGuardWorldListener(this));
        for (World world : Sponge.getServer().getWorlds()) {
            worldListener.initWorld(world);
        }
        worldListener.registerEvents();

        Bukkit.getScheduler().runTask(this, () -> {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                ProcessPlayerEvent event = new ProcessPlayerEvent(player);
                Events.fire(event);
            }
        }); //TODO: evaluate if this is neccesary, could be laggy if ported

        ((SimpleFlagRegistry) WorldGuard.getInstance().getFlagRegistry()).setInitialized(true);

    }

    /*@Override
    public void onDisable() {
        WorldGuard.getInstance().disable();
        this.getServer().getScheduler().cancelTasks(this);
    }*/

    @Listener(order = Order.FIRST)
    public boolean onCommand(SendCommandEvent event) {

        CommandSource sender = (CommandSource) event.getSource();
        String cmd = event.getCommand();
        String label;
        String[] args;
        try {
            Actor actor = wrapCommandSender(sender);
            try {
                commands.execute(cmd.getName(), args, actor, actor);
            } catch (Throwable t) {
                Throwable next = t;
                do {
                    try {
                        WorldGuard.getInstance().getExceptionConverter().convert(next);
                    } catch (org.enginehub.piston.exception.CommandException pce) {
                        if (pce.getCause() instanceof CommandException) {
                            throw ((CommandException) pce.getCause());
                        }
                    }
                    next = next.getCause();
                } while (next != null);

                throw t;
            }
        } catch (CommandPermissionsException e) {
            sender.sendMessage(Text.of(TextColors.RED + "You don't have permission."));
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(Text.of(TextColors.RED + e.getUsage()));
        } catch (CommandUsageException e) {
            sender.sendMessage(Text.of(TextColors.RED + e.getMessage()));
            sender.sendMessage(Text.of(TextColors.RED + e.getUsage()));
        } catch (WrappedCommandException e) {
            sender.sendMessage(Text.of(TextColors.RED + e.getCause().getMessage()));
        } catch (CommandException e) {
            sender.sendMessage(Text.of(TextColors.RED + e.getMessage()));
        }

        return true;
    }

    /**
     * Check whether a player is in a group.
     * This calls the corresponding method in PermissionsResolverManager
     *
     * @param player The player to check
     * @param group The group
     * @return whether {@code player} is in {@code group}
     */
    public boolean inGroup(User player, String group) {
        List<SubjectReference> sR = player.getParents();
        for(int i = 0; i < sR.size(); i++){
            if(sR.get(i).getSubjectIdentifier().equals(group)){
                return true;
            }
        }

        return false;

    }

    /**
     * Get the groups of a player.
     * This calls the corresponding method in PermissionsResolverManager.
     * @param player The player to check
     * @return The names of each group the playe is in.
     */
    public String[] getGroups(User player) {
        try {
            List<String> list = new ArrayList<>();
            player.getParents().forEach(subjectReference -> {
                list.add(subjectReference.getSubjectIdentifier());
            });
            return (String[])list.toArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return new String[0];
        }
    }

    /**
     * Checks permissions.
     *
     * @param sender The sender to check the permission on.
     * @param perm The permission to check the permission on.
     * @return whether {@code sender} has {@code perm}
     */
    public boolean hasPermission(CommandSource sender, String perm) {

        return sender.hasPermission(perm);
        //return false;
    }

    /**
     * Checks permissions and throws an exception if permission is not met.
     *
     * @param sender The sender to check the permission on.
     * @param perm The permission to check the permission on.
     * @throws CommandPermissionsException if {@code sender} doesn't have {@code perm}
     */
    public void checkPermission(CommandSource sender, String perm)
            throws CommandPermissionsException {
        if (!hasPermission(sender, perm)) {
            throw new CommandPermissionsException();
        }
    }

    /**
     * Gets a copy of the WorldEdit plugin.
     *
     * @return The WorldEditPlugin instance
     * @throws CommandException If there is no WorldEditPlugin available
     */
    public SpongeWorldEdit getWorldEdit() throws CommandException {
        Optional<PluginContainer> worldEdit = Sponge.getPluginManager().getPlugin("WorldEdit");
        if (!worldEdit.isPresent()) {
            throw new CommandException("WorldEdit does not appear to be installed.");
        }

        return SpongeWorldEdit.inst();
    }

    /**
     * Wrap a player as a LocalPlayer.
     *
     * @param player The player to wrap
     * @return The wrapped player
     */
    public LocalPlayer wrapPlayer(Player player) {
        return new SpongePlayer(this, player);
    }

    /**
     * Wrap a player as a LocalPlayer.
     *
     * @param player The player to wrap
     * @param silenced True to silence messages
     * @return The wrapped player
     */
    public LocalPlayer wrapPlayer(Player player, boolean silenced) {
        return new SpongePlayer(this, player, silenced);
    }

    public Actor wrapCommandSender(CommandSource sender) {
        if (sender instanceof Player) {
            return wrapPlayer((Player) sender);
        }

        try {
            //TODO: fix
            return new SpongeCommandSender(getWorldEdit(), sender);
        } catch (CommandException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CommandSource unwrapActor(Actor sender) {
        if (sender instanceof SpongePlayer) {
            return ((SpongePlayer) sender).getPlayer();
        } else if (sender instanceof ConsoleSource) {
            return Sponge.getServer().getConsole(); // TODO Fix
        } else {
            throw new IllegalArgumentException("Unknown actor type. Please report");
        }
    }

    /**
     * Wrap a player as a LocalPlayer.
     *
     * <p>This implementation is incomplete -- permissions cannot be checked.</p>
     *
     * @param player The player to wrap
     * @return The wrapped player
     */
    public LocalPlayer wrapOfflinePlayer(User player) {
        return new SpongeOfflinePlayer(this, player);
    }

    /**
     * Return a protection query helper object that can be used by another
     * plugin to test whether WorldGuard permits an action at a particular
     * place.
     *
     * @return an instance
     */
    public ProtectionQuery createProtectionQuery() {
        return new ProtectionQuery();
    }

    /**
     * Configure WorldGuard's loggers.
     */
    private void configureLogger() {
        RecordMessagePrefixer.register(Logger.getLogger("com.sk89q.worldguard"), "[WorldGuard] ");
    }

    /**
     * Create a default configuration file from the .jar.
     *
     * @param actual The destination file
     * @param defaultName The name of the file inside the jar's defaults folder
     */
    public void createDefaultConfiguration(File actual, String defaultName) {

        // Make parent directories
        File parent = actual.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        if (actual.exists()) {
            return;
        }

        InputStream input = null;
        /*try {
            JarFile file = new JarFile(getFile()); //TODO: bruh
            ZipEntry copy = file.getEntry("defaults/" + defaultName);
            if (copy == null) throw new FileNotFoundException();
            input = file.getInputStream(copy);
        } catch (IOException e) {*/
            WorldGuard.logger.severe("Unable to read default configuration: " + defaultName);
        //}

        if (input != null) {
            FileOutputStream output = null;

            try {
                output = new FileOutputStream(actual);
                byte[] buf = new byte[8192];
                int length = 0;
                while ((length = input.read(buf)) > 0) {
                    output.write(buf, 0, length);
                }

                WorldGuard.logger.info("Default configuration file written: "
                        + actual.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    input.close();
                } catch (IOException ignore) {
                }

                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException ignore) {
                }
            }
        }
    }

    public PlayerMoveListener getPlayerMoveListener() {
        return playerMoveListener;
    }

}
