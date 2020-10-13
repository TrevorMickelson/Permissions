package com.codepunisher.permissions.commands;

import com.codepunisher.permissions.PermMain;
import com.codepunisher.permissions.models.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PermTabComplete implements TabCompleter
{
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (sender.hasPermission("mcaim.admin"))
        {
            List<String> list = new ArrayList<>();
            int length = args.length;

            // If argument is only /perm <arg>
            if (length == 1) {
                String[] permArgs = { "addrank", "removerank", "addperm", "removeperm", "info" };

                for (String string : permArgs) {
                    if (string.toLowerCase().startsWith(args[0].toLowerCase()))
                        list.add(string);
                }
            }

            if (length == 2) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase().startsWith(args[1].toLowerCase()))
                        list.add(p.getName());
                }
            }

            // If argument is /perm <arg> <player> <arg>
            if (length == 3) {
                // First argument as lower case (for switch statement)
                switch (args[0].toLowerCase())
                {
                    case "addrank":
                    case "removerank":
                        for (Rank rank : PermMain.getInstance().getRankManager().ranks.values()) {
                            String name = rank.getName().toLowerCase();
                            if (name.startsWith(args[2].toLowerCase()))
                                list.add(name);
                        }
                        break;

                    case "addperm":
                    case "removeperm":
                        list.add("<insert-perm>");
                        break;
                }
            }

            return list;
        }
        return null;
    }
}
