package com.jonahseguin.drink.parametric;

import com.jonahseguin.drink.command.DrinkCommand;
import com.jonahseguin.drink.command.DrinkCommandService;
import com.jonahseguin.drink.exception.CommandStructureException;
import com.jonahseguin.drink.exception.MissingProviderException;

public class ProviderAssigner {

    private final DrinkCommandService commandService;

    public ProviderAssigner(DrinkCommandService commandService) {
        this.commandService = commandService;
    }

    public DrinkProvider<?>[] assignProvidersFor(DrinkCommand drinkCommand) throws MissingProviderException, CommandStructureException {
        CommandParameters parameters = drinkCommand.getParameters();
        DrinkProvider<?>[] providers = new DrinkProvider<?>[parameters.getParameters().length];
        for (int i = 0; i < parameters.getParameters().length; i++) {

            CommandParameter param = parameters.getParameters()[i];
            if (param.isRequireLastArg() && !parameters.isLastArgument(i)) {
                throw new CommandStructureException("Parameter " + param.getParameter().getName() + " [argument " + i + "] (" + param.getParameter().getType().getSimpleName() + ") in method '" + drinkCommand.getMethod().getName() + "' must be the last argument in the method.");
            }

            BindingContainer<?> bindings = commandService.getBindingsFor(param.getType());
            if (bindings != null) {
                DrinkProvider<?> provider = null;
                for (DrinkBinding<?> binding : bindings.getBindings()) {
                    if (binding.canProvideFor(param)) {
                        provider = binding.getProvider();
                        break;
                    }
                }
                if (provider != null) {
                    providers[i] = provider;
                } else {
                    throw new MissingProviderException("No provider bound for " + param.getType().getSimpleName() + " for parameter " + i + " for method " + drinkCommand.getMethod().getName());
                }
            } else {
                throw new MissingProviderException("No provider bound for " + param.getType().getSimpleName());
            }
        }
        return providers;
    }

}
