package com.jonahseguin.drink.command;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class DrinkCommandContainer extends Command implements PluginIdentifiableCommand {

    private final DrinkCommandService commandService;
    private final Object object;
    private final String name;
    private final Set<String> aliases;
    private final Map<String, DrinkCommand> commands;
    private final DrinkCommand defaultCommand;
    private final DrinkCommandExecutor executor;
    private final DrinkTabCompleter tabCompleter;
    private boolean overrideExistingCommands = true;
    private boolean defaultCommandIsHelp = false;

    public DrinkCommandContainer(DrinkCommandService commandService, Object object, String name, Set<String> aliases, Map<String, DrinkCommand> commands) {
        super(name, "", "/" + name, new ArrayList<>(aliases));
        this.commandService = commandService;
        this.object = object;
        this.name = name;
        this.aliases = aliases;
        this.commands = commands;
        this.defaultCommand = calculateDefaultCommand();
        this.executor = new DrinkCommandExecutor(commandService, this);
        this.tabCompleter = new DrinkTabCompleter(commandService, this);
        if (defaultCommand != null) {
            setUsage("/" + name + " " + defaultCommand.getGeneratedUsage());
            setDescription(defaultCommand.getDescription());
            setPermission(defaultCommand.getPermission());
        }
    }

    public final DrinkCommandContainer registerSub(@Nonnull Object handler) {
        return commandService.registerSub(this, handler);
    }

    public List<String> getCommandSuggestions(@Nonnull String prefix) {
        Preconditions.checkNotNull(prefix, "Prefix cannot be null");
        final String p = prefix.toLowerCase();
        List<String> suggestions = new ArrayList<>();
        for (DrinkCommand c : commands.values()) {
            if (c.isHidden()) continue;
            for (String alias : c.getAllAliases()) {
                if (alias.length() > 0) {
                    if (p.length() == 0 || alias.toLowerCase().startsWith(p)) {
                        suggestions.add(alias);
                    }
                }
            }
        }
        return suggestions;
    }

    private DrinkCommand calculateDefaultCommand() {
        for (DrinkCommand dc : commands.values()) {
            if (dc.getName().length() == 0 || dc.getName().equals(DrinkCommandService.DEFAULT_KEY)) {
                // assume default!
                return dc;
            }
        }
        return null;
    }

    @Nullable
    public DrinkCommand get(@Nonnull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        return commands.get(commandService.getCommandKey(name));
    }

    @Nullable
    public DrinkCommand getByKeyOrAlias(@Nonnull String key) {
        Preconditions.checkNotNull(key, "Key cannot be null");
        if (commands.containsKey(key)) {
            return commands.get(key);
        }
        for (DrinkCommand drinkCommand : commands.values()) {
            if (drinkCommand.getAliases().contains(key)) {
                return drinkCommand;
            }
        }
        return null;
    }

    /**
     * Gets a sub-command based on given arguments and also returns the new actual argument values
     * based on the arguments that were consumed for the sub-command key
     *
     * @param args the original arguments passed in
     * @return the DrinkCommand (if present, Nullable) and the new argument array
     */
    @Nullable
    public Map.Entry<DrinkCommand, String[]> getCommand(String[] args) {
        for (int i = (args.length - 1); i >= 0; i--) {
            String key = commandService.getCommandKey(StringUtils.join(Arrays.asList(Arrays.copyOfRange(args, 0, i + 1)), ' '));
            DrinkCommand drinkCommand = getByKeyOrAlias(key);
            if (drinkCommand != null) {
                return new AbstractMap.SimpleEntry<>(drinkCommand, Arrays.copyOfRange(args, i + 1, args.length));
            }
        }
        return new AbstractMap.SimpleEntry<>(getDefaultCommand(), args);
    }

    @Nullable
    public DrinkCommand getDefaultCommand() {
        return defaultCommand;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return executor.onCommand(commandSender, this, s, strings);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return tabCompleter.onTabComplete(sender, this, alias, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        return tabCompleter.onTabComplete(sender, this, alias, args);
    }

    @Override
    public Plugin getPlugin() {
        return commandService.getPlugin();
    }

    public DrinkCommandService getCommandService() {
        return commandService;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String getName() {
        return name;
    }

    public Set<String> getDrinkAliases() {
        return aliases;
    }

    public Map<String, DrinkCommand> getCommands() {
        return commands;
    }

    public DrinkCommandExecutor getExecutor() {
        return executor;
    }

    public DrinkTabCompleter getTabCompleter() {
        return tabCompleter;
    }

    public boolean isOverrideExistingCommands() {
        return overrideExistingCommands;
    }

    public DrinkCommandContainer setOverrideExistingCommands(boolean overrideExistingCommands) {
        this.overrideExistingCommands = overrideExistingCommands;
        return this;
    }

    public boolean isDefaultCommandIsHelp() {
        return defaultCommandIsHelp;
    }

    public DrinkCommandContainer setDefaultCommandIsHelp(boolean defaultCommandIsHelp) {
        this.defaultCommandIsHelp = defaultCommandIsHelp;
        return this;
    }
}
