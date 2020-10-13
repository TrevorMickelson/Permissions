package com.codepunisher.permissions.commands;

import com.codepunisher.mcaimcore.CommandHandler;
import com.codepunisher.mcaimcore.api.HelpersAPI;
import com.codepunisher.permissions.PermMain;
import com.codepunisher.permissions.models.PermUser;
import com.codepunisher.permissions.models.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

public class PermCommand extends CommandHandler
{
    // Instance of main class
    private PermMain plugin = PermMain.getInstance();

    public PermCommand()
    {
        super(PermMain.getInstance(), new String[] { "perm" },  "mcaim.admin", true,
              new String[] {
                  "§d** §5§lPermission Commands §d**",
                  "§d/Perm §8- §fView this page",
                  "§d/Perm addrank <player> <rank> §8- §fAdd rank to player",
                  "§d/Perm removerank <player> <rank> §8- §fRemove rank from player",
                  "§d/Perm addperm <player> <perm> §8- §fAdd permission to player",
                  "§d/Perm removeperm <player> <perm> §8- §fRemove permission from player",
                  "§d/Perm info <player> §8- §fView rank/perms of player"
              }, false);
    }

    @Override
    public void execute(CommandSender sender, Command cmd, String[] args)
    {
        // Prefixes for pretty look
        String darkPrefix = "§4§lPerms §8> §c";
        String brightPrefix = "§5§lPerms §8> §d";

        // Shit for shit to do shit
        String firstArg = args[0];
        String name = args[1];
        UUID uuid = HelpersAPI.getStringUUID(name);

        // Perm user instance
        PermUser permUser = plugin.getPermUserManager().getPermUser(uuid);

        if (permUser == null)
            permUser = plugin.getPermUserManager().getOfflineUserPerm(uuid);

        // Ranks and permissions list
        List<Rank> ranks = permUser.getRanks();
        List<String> permissions = permUser.getPersonalPerms();

        switch (firstArg.toLowerCase())
        {
            case "addrank":
            case "removerank":
                Rank rank = plugin.getRankManager().getRank(args[2]);

                // If to add rank or not
                boolean addRank = args[0].equalsIgnoreCase("addrank");

                if (addRank)
                {
                    // Making sure user doesn't already have rank
                    if (!ranks.contains(rank)) {
                        permUser.addRank(rank);
                        sender.sendMessage(brightPrefix +  "You have given §f" + name + " §dthe rank §f" + rank.getName() + "§d!");
                    } else {
                        sender.sendMessage(darkPrefix + "That user already has §f" + rank.getName() + "§c!");
                    }
                }
                else
                {
                    // Making sure user has rank
                    if (ranks.contains(rank)) {
                        // Not allowing them to remove the default rank
                        if (!args[2].equalsIgnoreCase("default")) {
                            permUser.removeRank(rank);
                            sender.sendMessage(brightPrefix + "You have removed the rank §f" + rank.getName() + " §dfrom §f" + name + "§d!");
                        } else {
                            sender.sendMessage(darkPrefix + "The default rank cannot be removed!");
                        }
                    } else {
                        sender.sendMessage(darkPrefix + "That user doesn't have that rank!");
                    }
                }
                break;

            case "addperm":
            case "removeperm":
                String perm = args[2];

                // If to add perm or not
                boolean addPerm = args[0].equalsIgnoreCase("addperm");

                if (addPerm)
                {
                    // Making sure user doesn't already have permission
                    if (!permissions.contains(perm)) {
                        boolean value = !perm.startsWith("-");
                        permUser.addPerm(perm, value);
                        sender.sendMessage(brightPrefix +  "You have given §f" + name + " §dthe permission §f" + perm + "§d!");
                    } else {
                        sender.sendMessage(darkPrefix + "That user already has the permission §f" + perm + "§c!");
                    }
                }
                else
                {
                    // Making sure user has permission
                    if (permissions.contains(perm)) {
                        permUser.removePerm(perm);
                        sender.sendMessage(brightPrefix + "You have removed the permission §f" + perm + " §dfrom §f" + name + "§d!");
                    } else {
                        sender.sendMessage(darkPrefix + "That user doesn't have that permission!");
                    }
                }
                break;

            case "info":
                sender.sendMessage("§5§lPermission Info: §f" + name);
                sender.sendMessage("§dRanks");
                for (Rank r : ranks)
                    sender.sendMessage("§8- §f" + r.getName());

                sender.sendMessage("§dPermissions");
                for (String p : permissions)
                    sender.sendMessage("§8- §f" + p);

                break;
        }
    }
}
