package com.codepunisher.permissions;

import com.codepunisher.permissions.util.PermUserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PermissionJoin implements Listener
{
    // Easy access to manager class
    private PermUserManager permUserManager = PermMain.getInstance().getPermUserManager();

    @EventHandler
    public void userJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        permUserManager.addPermUser(uuid, permUserManager.getOfflineUserPerm(uuid));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        permUserManager.removePermUser(uuid);
    }
}
