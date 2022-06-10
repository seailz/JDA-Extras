package dev.negativekb.api.listener;

import dev.negativekb.api.commands.SlashCommand;
import dev.negativekb.api.commands.internal.CommandMap;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class SlashCommandListener extends ListenerAdapter {

    private final CommandMap commandMap;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null)
            return;

        String id = guild.getId();
        Collection<SlashCommand> serverCommands = commandMap.getServerCommands(id);
        Optional<SlashCommand> firstServerCommand = serverCommands.stream()
                .filter(command -> command.getName().equalsIgnoreCase(event.getName()) ||
                        command.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(event.getName())))
                .findFirst();

        if (firstServerCommand.isPresent()) {
            firstServerCommand.get().runCommand(event);
            return;
        }

        Collection<SlashCommand> globalCommands = commandMap.getGlobalCommands();
        globalCommands.stream().filter(command -> command.getName().equalsIgnoreCase(event.getName()) ||
                        command.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(event.getName())))
                .findFirst().ifPresent(command -> command.runCommand(event));
    }
}
