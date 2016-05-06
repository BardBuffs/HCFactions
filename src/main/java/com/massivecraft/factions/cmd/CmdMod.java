package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.zcore.util.TL;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CmdMod extends FCommand {

    public CmdMod() {
        super();
        this.aliases.add("mod");

        this.optionalArgs.put("player name", "name");
        //this.optionalArgs.put("", "");

        this.permission = Permission.MOD.node;
        this.disableOnLock = true;

        senderMustBePlayer = false;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = true;
    }

    @Override
    public void perform() {
        FPlayer you = this.argAsBestFPlayerMatch(0);

        if (you == null) {
            TextComponent base = new TextComponent(TL.COMMAND_MOD_CANDIDATES.toString());
            base.setColor(ChatColor.GOLD);

            for (FPlayer player : myFaction.getFPlayersWhereRole(Role.NORMAL)) {
                String s = player.getName();

                TextComponent text = new TextComponent(s + " ");
                text.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + Conf.baseCommandAliases.get(0) + " mod " + s));
                text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(TL.COMMAND_MOD_CLICKTOPROMOTE.toString() + s).create()));
                text.setColor(ChatColor.WHITE);
                base.addExtra(text);
            }

            fme.getPlayer().spigot().sendMessage(base);
            return;
        }

        boolean permAny = Permission.MOD_ANY.has(sender, false);
        Faction targetFaction = you.getFaction();

        if (targetFaction != myFaction && !permAny) {
            msg(TL.COMMAND_MOD_NOTMEMBER, you.describeTo(fme, true));
            return;
        }

        if (fme != null && fme.getRole() != Role.ADMIN && !permAny) {
            msg(TL.COMMAND_MOD_NOTADMIN);
            return;
        }

        if (you == fme && !permAny) {
            msg(TL.COMMAND_MOD_SELF);
            return;
        }

        if (you.getRole() == Role.ADMIN) {
            msg(TL.COMMAND_MOD_TARGETISADMIN);
            return;
        }

        if (you.getRole() == Role.MODERATOR) {
            // Revoke
            you.setRole(Role.NORMAL);
            targetFaction.msg(TL.COMMAND_MOD_REVOKED, you.describeTo(targetFaction, true));
            msg(TL.COMMAND_MOD_REVOKES, you.describeTo(fme, true));
        } else {
            // Give
            you.setRole(Role.MODERATOR);
            targetFaction.msg(TL.COMMAND_MOD_PROMOTED, you.describeTo(targetFaction, true));
            msg(TL.COMMAND_MOD_PROMOTES, you.describeTo(fme, true));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MOD_DESCRIPTION;
    }

}
