package com.codepunisher.permissions;

import com.codepunisher.mcaimcore.configuration.ConfigAPI;
import com.codepunisher.permissions.commands.PermCommand;
import com.codepunisher.permissions.commands.PermTabComplete;
import com.codepunisher.permissions.util.PermUserManager;
import com.codepunisher.permissions.util.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class PermMain extends JavaPlugin
{
    /** Creating instance of main class */
    private static PermMain permMain;
    public static PermMain getInstance() { return permMain; }

    /** Instance of permission data manager class */
    private PermUserManager permUserManager = new PermUserManager();
    public PermUserManager getPermUserManager() { return this.permUserManager; }

    /** Instance of rank manager class */
    private RankManager rankManager = new RankManager();
    public RankManager getRankManager() { return this.rankManager; }

    @Override
    public void onEnable()
    {
        permMain = this;

        // Loading rank/user file
        ConfigAPI.loadFile(this, "ranks.yml");
        ConfigAPI.loadFile(this, "players.yml");

        // Registering listener class
        getServer().getPluginManager().registerEvents(new PermissionJoin(), this);

        // Registering commands
        new PermCommand();
        Objects.requireNonNull(getCommand("perm")).setTabCompleter(new PermTabComplete());

        // Registering ranks
        getRankManager().initializeRanks();

        // Creating perm user objects for online users
        // This is to make the plugin reloadable
        // Though I wouldn't advice doing this with a
        // large amount of players online
        for (Player player : Bukkit.getOnlinePlayers()) {
            getPermUserManager().addPermUser(player.getUniqueId(), getPermUserManager().getOfflineUserPerm(player.getUniqueId()));
        }
    }

    public void onDisable() {}
}
