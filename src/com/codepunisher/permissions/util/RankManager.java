package com.codepunisher.permissions.util;

import com.codepunisher.mcaimcore.configuration.ConfigAPI;
import com.codepunisher.mcaimcore.configuration.DataFile;
import com.codepunisher.permissions.PermMain;
import com.codepunisher.permissions.models.Rank;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RankManager
{
    /** Stores all rank objects in hash map with their name/rank attached */
    public HashMap<String, Rank> ranks = new HashMap<>();

    /** Returns rank object to use */
    public Rank getRank(String string) { return ranks.get(string); }

    /** Initializes and stores all ranks (ONLY USED ON ENABLE) */
    public void initializeRanks() {
        DataFile rankFile = ConfigAPI.getDataFile(PermMain.getInstance(), "ranks.yml");

        if (rankFile.getData().isSet("ranks")) {
            // Setting up ranks
            for (String rank : Objects.requireNonNull(rankFile.getData().getConfigurationSection("ranks")).getKeys(false)) {
                int priority = rankFile.getData().getInt("ranks." + rank + ".priority");
                String prefix = rankFile.getData().getString("ranks." + rank + ".prefix");
                List<String> inheritance = rankFile.getData().getStringList("ranks." + rank + ".inheritance");
                List<String> permissions = rankFile.getData().getStringList("ranks." + rank + ".permissions");
                this.ranks.put(rank, new Rank(rank, priority, prefix, inheritance, permissions));
            }

            // Setting up rank inheritance
            for (Rank rank : ranks.values()) {
                // Making sure there's an inheritance set
                if (!rank.getInheritance().isEmpty()) {
                    // Adding permissions from each inheritance
                    for (String rankString : rank.getInheritance()) {
                        Rank rankInheritance = getRank(rankString);
                        rank.getPermissions().addAll(rankInheritance.getPermissions());
                    }
                }
            }
        }
    }
}
