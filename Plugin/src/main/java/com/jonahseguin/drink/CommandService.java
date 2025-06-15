package com.jonahseguin.drink;

import com.jonahseguin.drink.command.DrinkAuthorizer;
import com.jonahseguin.drink.command.DrinkCommandContainer;
import com.jonahseguin.drink.modifier.DrinkModifier;
import com.jonahseguin.drink.parametric.binder.DrinkBinder;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;

/**
 * Drink CommandServices are {@link org.bukkit.plugin.java.JavaPlugin}-specific.
 * Meaning one per JavaPlugin (Spigot/Bukkit Plugin).
 * Since the commands are associated with the specified command,
 * and are dynamically registered into the server's CommandMap
 * <p>
 * See {@link Drink#get(JavaPlugin)} for getting an instance of a functional {@link CommandService}
 */
public interface CommandService {

    /**
     * Register a Drink command into the Drink Command Service
     *
     * @param handler Object that has the {@link com.jonahseguin.drink.annotation.Command} annotated methods
     * @param name    The name of the command to register.
     *                The names of methods within the handler object will be sub-commands to this name.
     *                If you want to create a default command (just /name), set the name here and in the
     *                {@link com.jonahseguin.drink.annotation.Command} annotation set name = ""
     * @param aliases (Optional) A list of alternate command names that can be used
     * @return The {@link DrinkCommandContainer} containing the command you registered
     */
    DrinkCommandContainer register(@Nonnull Object handler, @Nonnull String name, @Nullable String... aliases);

    /**
     * Register a sub-command into the specified root command container
     *
     * @param root    The {@link DrinkCommandContainer} super-command to register your sub-commands into
     * @param handler The object that has the {@link com.jonahseguin.drink.annotation.Command}
     *                annotated methods to register
     * @return The {@link DrinkCommandContainer} containing the command you registered (same as the root passed in)
     */
    DrinkCommandContainer registerSub(@Nonnull DrinkCommandContainer root, @Nonnull Object handler);

    /**
     * Must be called after all of you commands have been registered into Drink with
     * {@link #register(Object, String, String...)} and {@link #registerSub(DrinkCommandContainer, Object)}
     * <p>
     * This registers the command into the Bukkit/Spigot CommandMap so that they can be executed on the server.
     */
    void registerCommands();

    /**
     * Start binding a class type to a {@link com.jonahseguin.drink.parametric.DrinkProvider} or instance.
     *
     * @param type The Class type to bind to
     * @param <T>  The type of class
     * @return A {@link DrinkBinder} instance to finish the binding
     */
    <T> DrinkBinder<T> bind(@Nonnull Class<T> type);

    /**
     * Registers a modifier to modify provided arguments for a specific type
     *
     * @param annotation The annotation to use for the modifier (must have {@link com.jonahseguin.drink.annotation.Modifier} annotated in it's class)
     * @param type       The type to modify
     * @param modifier   The modifier
     * @param <T>        The type of class to modify
     */
    <T> void registerModifier(@Nonnull Class<? extends Annotation> annotation, @Nonnull Class<T> type, @Nonnull DrinkModifier<T> modifier);

    /**
     * @param name The primary name of the {@link DrinkCommandContainer} you want to get
     * @return {@link Nullable} The {@link DrinkCommandContainer} with the specified name
     */
    @Nullable
    DrinkCommandContainer get(@Nonnull String name);

    /**
     * Set the authorizer that Drink uses.
     * This will allow you to edit the behavior for checking if a {@link org.bukkit.command.CommandSender} or
     * {@link org.bukkit.entity.Player} has permission to run a command.
     * You can also edit the no-permission message by modifying this, or use {@link DrinkAuthorizer#setNoPermissionMessage(String)}
     *
     * @param authorizer {@link Nonnull} A {@link DrinkAuthorizer} instance to be used for
     *                   checking authorization for command execution
     */
    void setAuthorizer(@Nonnull DrinkAuthorizer authorizer);

}
