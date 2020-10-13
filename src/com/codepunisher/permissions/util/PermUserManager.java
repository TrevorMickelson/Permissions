package com.codepunisher.permissions.util;

import com.codepunisher.mcaimcore.configuration.ConfigAPI;
import com.codepunisher.mcaimcore.configuration.DataFile;
import com.codepunisher.permissions.PermMain;
import com.codepunisher.permissions.models.PermUser;
import com.codepunisher.permissions.models.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * This class is for handling
 * permission user data storage
 *
 * Handles online data storage
 * and file data storage
 */
public class PermUserManager
{
    /** Stores all permission users */
    private HashMap<UUID, PermUser> permUsers = new HashMap<>();

    /**
     * Gets the permission user object
     * @param uuid players uuid
     * @return PermUser object
     */
    public PermUser getPermUser(UUID uuid) { return this.permUsers.get(uuid); }

    /** Adds user to perm users hash map */
    public void addPermUser(UUID uuid, PermUser permUser) { this.permUsers.put(uuid, permUser); }

    /** Removing perm user */
    public void removePermUser(UUID uuid) { this.permUsers.remove(uuid); }

    /**
     * Returns user perm object
     * for an offline user
     */
    public PermUser getOfflineUserPerm(UUID uuid) {
        // Data file
        DataFile dataFile = ConfigAPI.getDataFile(PermMain.getInstance(), "players.yml");

        // Section in file to get
        String section = "players." + uuid.toString() + ".";

        // Player instance
        Player player = Bukkit.getPlayer(uuid);

        // Checking if player is stored in file or not
        if (dataFile.getData().isSet("players." + uuid.toString())) {
            int priority = dataFile.getData().getInt(section + "priority");
            String prefix = dataFile.getData().getString(section + "prefix");
            List<String> stringRanks = dataFile.getData().getStringList(section + "ranks");
            List<String> personalPerms = dataFile.getData().getStringList(section + "personalperms");

            List<Rank> ranks = new ArrayList<>();
            for (String string : stringRanks) {
                Rank rank = PermMain.getInstance().getRankManager().getRank(string);

                if (rank != null)
                    ranks.add(rank);
            }

            // Perm user object
            PermUser permUser = new PermUser(uuid);
            permUser.setPriority(priority);
            permUser.setPrefix(prefix);
            permUser.setRanks(ranks);
            permUser.setPersonalPerms(personalPerms);

            // Setting custom name and permissions
            if (player != null) {
                permUser.setCustomName(player, prefix);

                // Setting user permission for user (Based on rank and user perms)
                for (Rank rank : ranks) {
                    for (String permission : rank.getPermissions()) {
                        boolean value = !permission.startsWith("-");

                        // This check is in case the same permission exists on multiple ranks
                        if (!personalPerms.contains(permission)) {
                            permUser.getAttachment().setPermission(permission, value);
                        }
                    }
                }

                // Setting personal permissions
                for (String permission : personalPerms) {
                    boolean value = !permission.startsWith("-");
                    permUser.getAttachment().setPermission(permission, value);
                }
            }

            return permUser;
        } else {
            return new PermUser(uuid);
        }
    }

    /** Updates user data in file */
    public void updateUserFile(PermUser permUser, String type) {
        PermMain.getInstance().getServer().getScheduler().runTaskAsynchronously(PermMain.getInstance(), ()-> {
            // Data file
            DataFile dataFile = ConfigAPI.getDataFile(PermMain.getInstance(), "players.yml");

            // Perm user data
            UUID uuid = permUser.getUuid();
            int priority = permUser.getPriority();
            String prefix = permUser.getPrefix();
            List<String> ranks = permUser.getRankNames();
            List<String> personalPerms = permUser.getPersonalPerms();

            // Section in file to set
            String section = "players." + uuid.toString() + ".";

            // Updating file
            if (dataFile.getData().isSet("players." + permUser.getUuid()))
            {
                switch (type.toLowerCase())
                {
                    case "ranks":
                        dataFile.getData().set(section + "ranks", ranks);
                        dataFile.getData().set(section + "priority", priority);
                        dataFile.getData().set(section + "prefix", prefix);
                        break;

                    case "personalperms":
                        dataFile.getData().set(section + "personalperms", personalPerms);
                        break;

                    case "remove":
                        dataFile.getData().set("players." + uuid.toString(), null);
                        break;
                }
            }
            else
            {
                dataFile.getData().set(section + "ranks", ranks);
                dataFile.getData().set(section + "priority", priority);
                dataFile.getData().set(section + "prefix", prefix);
                dataFile.getData().set(section + "personalperms", personalPerms);
            }

            // Saving data file
            dataFile.saveData();
        });
    }
}
