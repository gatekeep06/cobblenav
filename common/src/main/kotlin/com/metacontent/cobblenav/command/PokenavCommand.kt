package com.metacontent.cobblenav.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal

abstract class PokenavCommand : CobblenavCommand {
    companion object {
        const val BASE = "pokenav"
    }

    override fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(literal(BASE).then(afterBase()))
    }

    abstract fun afterBase(): ArgumentBuilder<CommandSourceStack, *>
}