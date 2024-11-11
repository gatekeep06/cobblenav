package com.metacontent.cobblenav.command.argument

import com.metacontent.cobblenav.client.gui.util.PokefinderSettings
import com.metacontent.cobblenav.util.cobblenavResource
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.SharedSuggestionProvider
import java.util.concurrent.CompletableFuture

class PokefidnerSettingsArgumentType : ArgumentType<PokefinderSettings> {
    companion object {
        const val DELIMITER = " "
        const val ASSIGNER = "="
        val ID = cobblenavResource("pokefinder_settings")
        fun settings() = PokefidnerSettingsArgumentType()
    }

    override fun parse(reader: StringReader): PokefinderSettings {
        val string = reader.remaining
        reader.cursor = reader.totalLength
        return PokefinderSettings.parse(string, DELIMITER, ASSIGNER)
    }

    override fun <S : Any> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        val sections = builder.remaining.split(DELIMITER).toMutableList()
        val notBlankSections = sections.filter { it.isNotBlank() }
        val settings = PokefinderSettings.SETTINGS.toMutableSet()
        settings.removeIf { setting -> sections.any { it.contains(setting) } }

        val currentSection = sections.lastOrNull() ?: return SharedSuggestionProvider.suggest(settings, builder)
        if (sections.any { it != currentSection && PokefinderSettings.SETTINGS.contains(it) }) {
            builder.suggest(notBlankSections.joinToString(DELIMITER) + ASSIGNER)
        }
        else {
            settings.filter { it.contains(currentSection) }.forEach {
                var argument = notBlankSections.joinToString(DELIMITER).removeSuffix(currentSection)
                if (argument.isNotBlank()) {
                    argument += DELIMITER
                }
                builder.suggest(argument + it)
            }
        }

        return builder.buildFuture()
    }
}