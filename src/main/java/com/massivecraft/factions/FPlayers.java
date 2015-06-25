package com.massivecraft.factions;

import com.massivecraft.factions.zcore.persist.json.JSONFPlayers;
import com.massivecraft.factions.zcore.persist.mongodb.MongoFPlayers;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class FPlayers {
    protected static FPlayers instance = getFPlayersImpl();

    public abstract void clean();

    public static FPlayers getInstance() {
        return instance;
    }

    private static FPlayers getFPlayersImpl() {
        switch (Conf.backEnd) {
            case MONGODB:
                return new MongoFPlayers();
            case JSON:
                return new JSONFPlayers();
        }
        return null;
    }

    public abstract Collection<FPlayer> getOnlinePlayers();

    public abstract FPlayer getByPlayer(Player player);

    public abstract Collection<FPlayer> getAllFPlayers();

    public abstract void forceSave();

    public abstract FPlayer getByOfflinePlayer(OfflinePlayer player);

    public abstract FPlayer getById(String string);

    public abstract void load();
}
