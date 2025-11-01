package dev.smartshub.shpets.plugin.command.handler.parameter;

import dev.smartshub.shpets.api.pet.Pet;
import dev.smartshub.shpets.api.pet.template.PetTemplate;
import dev.smartshub.shpets.api.registry.PetTemplateRegistry;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class PetParameterType implements ParameterType<BukkitCommandActor, PetTemplate> {

    private final PetTemplateRegistry petTemplateRegistry;

    public PetParameterType(PetTemplateRegistry petTemplateRegistry) {
        this.petTemplateRegistry = petTemplateRegistry;
    }

    @Override
    public PetTemplate parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String name = input.readString();
        var pet = petTemplateRegistry.get(name);
        if (pet == null) {
            throw new CommandErrorException("No such Pet: " + name);
        }
        return pet;
    }

    @Override
    public @NotNull SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return context -> petTemplateRegistry.getAll().stream()
                .map(PetTemplate::id)
                .toList();
    }
}
