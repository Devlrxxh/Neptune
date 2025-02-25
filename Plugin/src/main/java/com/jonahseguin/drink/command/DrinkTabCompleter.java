package com.jonahseguin.drink.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DrinkTabCompleter implements TabCompleter {

    private final DrinkCommandService commandService;
    private final DrinkCommandContainer container;

    public DrinkTabCompleter(DrinkCommandService commandService, DrinkCommandContainer container) {
        this.commandService = commandService;
        this.container = container;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(container.getName())) {
            Map.Entry<DrinkCommand, String[]> data = container.getCommand(args);
            if (data != null && data.getKey() != null) {
                String tabCompleting = "";
                int tabCompletingIndex = 0;
                if (data.getValue().length > 0) {
                    tabCompleting = data.getValue()[data.getValue().length - 1];
                    tabCompletingIndex = data.getValue().length - 1;
                }
                DrinkCommand drinkCommand = data.getKey();
                if (drinkCommand.getConsumingProviders().length > tabCompletingIndex) {
                    List<String> s = drinkCommand.getConsumingProviders()[tabCompletingIndex].getSuggestions(sender, tabCompleting);
                    if (s != null) {
                        List<String> suggestions = new ArrayList<>(s);
                        if (args.length == 0 || args.length == 1) {
                            String tC = "";
                            if (args.length > 0) {
                                tC = args[args.length - 1];
                            }
                            suggestions.addAll(container.getCommandSuggestions(tC));
                        }
                        return suggestions;
                    } else {
                        if (args.length == 0 || args.length == 1) {
                            String tC = "";
                            if (args.length > 0) {
                                tC = args[args.length - 1];
                            }
                            return container.getCommandSuggestions(tC);
                        }
                    }
                } else {
                    if (args.length == 0 || args.length == 1) {
                        String tC = "";
                        if (args.length > 0) {
                            tC = args[args.length - 1];
                        }
                        return container.getCommandSuggestions(tC);
                    }
                }
            } else {
                if (args.length == 0 || args.length == 1) {
                    String tC = "";
                    if (args.length > 0) {
                        tC = args[args.length - 1];
                    }
                    return container.getCommandSuggestions(tC);
                }
            }
        }
        return Collections.emptyList();
    }
}
