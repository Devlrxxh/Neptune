package com.jonahseguin.drink.command;

import org.bukkit.command.CommandSender;

public interface HelpFormatter {

    void sendHelpFor(CommandSender sender, DrinkCommandContainer container);
}
