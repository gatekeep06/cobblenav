package com.metacontent.cobblenav.command.argument

import com.metacontent.cobblenav.api.contact.title.TrainerTitle
import com.metacontent.cobblenav.api.contact.title.TrainerTitles
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

class TrainerTitleArgument : ArgumentType<TrainerTitle> {
    companion object {
        fun title() = TrainerTitleArgument()

        fun getTitle(context: CommandContext<CommandSourceStack>, argument: String): TrainerTitle {
            return context.getArgument(argument, TrainerTitle::class.java)
        }
    }

    override fun parse(reader: StringReader): TrainerTitle {
        val id = ResourceLocation.read(reader)
        val title = TrainerTitles.getTitle(id)
        return title!!
    }

    override fun <S : Any?> listSuggestions(
        context: CommandContext<S>,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        return CompletableFuture.supplyAsync {
            val idString = builder.remaining
            TrainerTitles.getStrict()
                .filter { it.id.toString().startsWith(idString) }
                .map { it.id.toString() }
                .forEach { builder.suggest(it) }
            return@supplyAsync builder.build()
        }
    }
}