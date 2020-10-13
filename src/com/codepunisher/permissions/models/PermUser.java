package com.codepunisher.permissions.models;

import com.codepunisher.permissions.PermMain;

import com.nametagedit.plugin.NametagEdit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermUser
{
    private UUID uuid;                                                      // User uuid
    private int priority;                                                   // Highest priority based on rank
    private String prefix;                                                  // Highest prefix based on rank
    private List<Rank> ranks;                                               // The users ranks
    private List<String> personalPerms;                                     // Permission user permissions
    private PermissionAttachment attachment;                                // Permission attachments

    // Constructor (fuck you the comment here looks good)
    public PermUser(UUID uuid) {
        this.uuid = uuid;
        this.priority = 0;
        this.ranks = new ArrayList<>();

        // Only adding attachment for online user
        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            this.attachment = player.addAttachment(PermMain.getInstance());
        }

        // Personal list of permissions (used for storing)
        this.personalPerms = new ArrayList<>();

        // Adding default rank to user
        addRank(PermMain.getInstance().getRankManager().getRank("default"));
    }

    public UUID getUuid() { return this.uuid; }

    public int getPriority() { return this.priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getPrefix() { return this.prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }

    public List<Rank> getRanks() { return this.ranks; }
    public void setRanks(List<Rank> ranks) { this.ranks = ranks; }

    public List<String> getPersonalPerms() { return this.personalPerms; }
    public void setPersonalPerms(List<String> personalPerms) { this.personalPerms = personalPerms; }

    public PermissionAttachment getAttachment() { return this.attachment; }

    // Returns string list of rank names
    public List<String> getRankNames() {
        List<String> list = new ArrayList<>();

        for (Rank rank : getRanks())
            list.add(rank.getName());

        return list;
    }

    // Getting users highest rank priority
    private Rank getHighestRankPriority() {
        Rank newRank = null;
        int max = 0;
        for (Rank rank : getRanks()) {
            if (rank.getPriority() > max) {
                max = rank.getPriority();
                newRank = rank;
            }
        }

        return newRank;
    }

    // --- ADDING/REMOVE RANK FROM USER --- //
    public void addRank(Rank rank) {
        // Adding perms (if the player is online)
        Player player = Bukkit.getPlayer(getUuid());
        boolean notNull = player != null;

        // Setting custom name if the new rank
        // Priority is higher than the current priority
        if (rank.getPriority() > getPriority()) {
            setPriority(rank.getPriority());
            setPrefix(rank.getPrefix());
        }

        if (notNull) {
            for (String permission : rank.getPermissions()) {
                boolean value = !permission.startsWith("-");

                // This check is in case the same permission exists on multiple ranks
                if (!getPersonalPerms().contains(permission)) {
                    getAttachment().setPermission(permission, value);
                }
            }

            setCustomName(player, rank.getPrefix());
        }

        this.ranks.add(rank);

        // Making sure user has more than one rank
        // (default) Before adding them to the file
        if (getRanks().size() > 1)
            PermMain.getInstance().getPermUserManager().updateUserFile(this, "ranks");
    }
    public void removeRank(Rank rank) {
        this.ranks.remove(rank);

        // Removing perms (if they player is online)
        Player player = Bukkit.getPlayer(getUuid());
        boolean notNull = player != null;

        if (notNull) {
            for (String permission : rank.getPermissions())
                getAttachment().unsetPermission(permission);
        }

        // Updating rank priority and display name (based on new highest priority)
        Rank newRank = getHighestRankPriority();

        if (newRank != null) {
            setPriority(newRank.getPriority());
            setPrefix(newRank.getPrefix());

            if (notNull)
                setCustomName(player, newRank.getPrefix());
        } else {
            setPriority(0);
            setPrefix("&7");

            if (notNull)
                setCustomName(player, getPrefix());
        }

        // Checking if I need to remove user from file
        // Basically if they have nothing that needs to be stored
        if (getRanks().size() <= 1 && getPersonalPerms().size() <= 0) {
            PermMain.getInstance().getPermUserManager().updateUserFile(this, "remove");
        } else {
            PermMain.getInstance().getPermUserManager().updateUserFile(this, "ranks");
        }
    }

    // --- ADDING/REMOVE PERMISSION FROM USER --- //
    public void addPerm(String permission, boolean value) {
        if (Bukkit.getPlayer(getUuid()) != null)
            getAttachment().setPermission(permission, value);

        getPersonalPerms().add(permission);
        PermMain.getInstance().getPermUserManager().updateUserFile(this, "personalperms");
    }
    public void removePerm(String permission) {
        if (Bukkit.getPlayer(getUuid()) != null)
            getAttachment().unsetPermission(permission);

        getPersonalPerms().remove(permission);

        // Checking if I need to remove user from file
        // Basically if they have nothing that needs to be stored
        if (getRanks().size() <= 1 && getPersonalPerms().size() <= 0) {
            PermMain.getInstance().getPermUserManager().updateUserFile(this, "remove");
        } else {
            PermMain.getInstance().getPermUserManager().updateUserFile(this, "ranks");
        }
    }

    // Setting custom name (using name tag edit)
    // I tried to not use name tag edit
    // However changing the display name above a players
    // head is THE HARDEST THING IN THE FUCKING WORLD
    public void setCustomName(Player player, String prefix) {
        player.setDisplayName(ChatColor.translateAlternateColorCodes('&', prefix + player.getName()));
        NametagEdit.getApi().applyTagToPlayer(player, true);
    }
}
